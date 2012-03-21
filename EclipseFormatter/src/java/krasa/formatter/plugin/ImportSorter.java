package krasa.formatter.plugin;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.MultiValuesMap;
import krasa.formatter.settings.Settings;
import krasa.formatter.utils.StringUtils;

import java.util.*;

/**
 * @author Vojtech Krasa
 */
public class ImportSorter {
    public static final int START_INDEX_OF_IMPORTS_PACKAGE_DECLARATION = 7;
    public static final String N = Settings.LINE_SEPARATOR;

    private List<String> importsOrder;

    public ImportSorter(List<String> importsOrder) {
        this.importsOrder = new ArrayList<String>(importsOrder);
    }

    public void sortImports(Document document) {
        String documentText = document.getText();
        //parse file
        Scanner scanner = new Scanner(documentText);
        int firstImportLine = 0;
        int lastImportLine = 0;
        int line = 0;
        List<String> imports = new ArrayList<String>();
        while (scanner.hasNext()) {
            line++;
            String next = scanner.nextLine();
            if (next == null) {
                break;
            }
            if (next.startsWith("import ")) {
                int i = next.indexOf(".");
                if (isNotValidImport(i)) {
                    continue;
                }
                if (firstImportLine == 0) {
                    firstImportLine = line;
                }
                lastImportLine = line;
                int endIndex = next.indexOf(";");
                imports.add(next.substring(START_INDEX_OF_IMPORTS_PACKAGE_DECLARATION, endIndex != -1 ? endIndex : next.length()));
            }
        }

        List<String> sortedImports = sortByEclipseStandard(imports);
        applyImportsToDocument(document, firstImportLine, lastImportLine, sortedImports);
    }

    private void applyImportsToDocument(Document document, int firstImportLine, int lastImportLine, List<String> strings) {
        Scanner scanner;
        boolean alreadySorted = false;
        scanner = new Scanner(document.getText());
        int line2 = 0;
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNext()) {
            line2++;
            String next = scanner.nextLine();
            if (next == null) {
                break;
            }
            if (line2 >= firstImportLine && line2 <= lastImportLine) {
                if (!alreadySorted) {
                    for (String string : strings) {
                        sb.append(string);
                    }
                }
                alreadySorted = true;
            } else {
                append(sb, next);
            }
        }
        document.setText(sb.toString());
    }

    protected List<String> sortByEclipseStandard(List<String> imports) {
        ImportsTemplate importsTemplate = new ImportsTemplate(importsOrder);
        List<String> notMatching = importsTemplate.filterMatchingImports(imports);
        notMatching.addAll(importsOrder);
        importsTemplate.mergeStaticImports(notMatching);
        importsTemplate.mergeNotMatchingItems(notMatching);
        importsTemplate.mergeMatchingItems();

        return importsTemplate.getResult();
    }

    private void append(StringBuilder sb, String next) {
        sb.append(next);
        sb.append(Settings.LINE_SEPARATOR);
    }

    private boolean isNotValidImport(int i) {
        return i <= START_INDEX_OF_IMPORTS_PACKAGE_DECLARATION;
    }

    class ImportsTemplate {
        List<String> template = new ArrayList<String>();
        MultiValuesMap<String, String> matchingImports = new MultiValuesMap<java.lang.String, java.lang.String>();
        Set<String> order = new HashSet<String>();

        ImportsTemplate(List<String> order) {
            template.addAll(order);
            this.order.addAll(order);
        }

        /**
         * returns not matching items and initializes internal state
         */
        public List<String> filterMatchingImports(List<String> imports) {
            ArrayList<String> notMatching = new ArrayList<String>();
            for (String anImport : imports) {
                String matchingImport = null;
                for (String orderItem : order) {
                    if (anImport.startsWith(orderItem)) {
                        if (matchingImport == null) {
                            matchingImport = orderItem;
                        } else {
                            matchingImport = StringUtils.betterMatching(matchingImport, orderItem, anImport);
                        }
                    }
                }
                if (matchingImport != null) {
                    matchingImports.put(matchingImport, anImport);
                } else {
                    notMatching.add(anImport);
                }
            }
            return notMatching;
        }

        /**
         * not matching means it does not match eny order item,
         * so it will be appended before or after order items
         */
        public void mergeNotMatchingItems(List<String> notMatching) {
            Collections.sort(notMatching);

            int firstIndexOfOrderItem = getFirstIndexOfOrderItem(notMatching);
            int indexOfOrderItem = 0;
            for (int i = 0; i < notMatching.size(); i++) {
                String notMatchingItem = notMatching.get(i);
                boolean isOrderItem = order.contains(notMatchingItem);
                if (isOrderItem) {
                    indexOfOrderItem = template.indexOf(notMatchingItem);
                } else {
                    if (indexOfOrderItem == 0) {
                        //insert before alphabetically first order item
                        template.add(firstIndexOfOrderItem , notMatchingItem);
                    } else {
                        //insert after the previous order item
                        template.add(indexOfOrderItem+1, notMatchingItem);
                        indexOfOrderItem++;
                    }
                }
            }
        }

        /**
         * gets first order item from input list, and finds out it's index in template
         */
        private int getFirstIndexOfOrderItem(List<String> notMatching) {
            int firstIndexOfOrderItem = 0;
            for (int i = 0; i < notMatching.size(); i++) {
                String notMatchingItem = notMatching.get(i);
                boolean isOrderItem = order.contains(notMatchingItem);
                if (isOrderItem) {
                    firstIndexOfOrderItem = template.indexOf(notMatchingItem);
                    break;
                }
            }
            return firstIndexOfOrderItem;
        }

        public void mergeMatchingItems() {
            for (int i = 0; i < template.size(); i++) {
                String item = template.get(i);
                if (order.contains(item)) {
                    //find matching items for order item
                    Collection<String> strings = matchingImports.get(item);
                    if (strings == null || strings.isEmpty()) {
                        //if there is none, just remove order item
                        template.remove(i);
                        i--;
                        continue;
                    }
                    ArrayList<String> matchingItems = new ArrayList<String>(strings);
                    Collections.sort(matchingItems);

                    //replace order item by matching import statements
                    //this is a mess and it is only a luck that it works :-]
                    template.remove(i);
                    if (i != 0 && !template.get(i - 1).equals(N)) {
                        template.add(i, N);
                        i++;
                    }
                    if (i + 1 < template.size() && !template.get(i + 1).equals(N)) {
                        template.add(i, N);
                    }
                    template.addAll(i, matchingItems);
                    if (i != 0 && !template.get(i - 1).equals(N)) {
                        template.add(i, N);
                    }

                }
            }
        }

        public List<String> getResult() {
            ArrayList<String> strings = new ArrayList<String>();

            for (String s : template) {
                if (s.equals(N)) {
                    strings.add(s);
                } else {
                    strings.add("import " + s + ";" + N);
                }
            }
            return strings;
        }

        public void mergeStaticImports(List<String> notMatching) {
            Collections.sort(notMatching);
            Collections.reverse(notMatching);

            for (int i = 0; i < notMatching.size(); i++) {
                String notMatchingItem = notMatching.get(i);
                if (notMatchingItem.startsWith("static ")) {
                    template.add(0, notMatchingItem);
                    notMatching.remove(i);
                    i--;
                }
            }
        }
    }
}

