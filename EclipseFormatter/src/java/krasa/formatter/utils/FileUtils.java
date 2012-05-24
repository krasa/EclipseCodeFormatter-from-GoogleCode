package krasa.formatter.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import krasa.formatter.eclipse.FileDoesNotExistsException;
import krasa.formatter.plugin.InvalidPropertyFile;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class FileUtils {
    private static final Logger LOG = Logger.getInstance(FileUtils.class.getName());

    public static boolean isWritable(@NotNull VirtualFile file,
                                     @NotNull Project project) {
        return !ReadonlyStatusHandler.getInstance(project)
                .ensureFilesWritable(file).hasReadonlyFiles();
    }

    public static boolean isWholeFile(int startOffset, int endOffset, String text) {
        return startOffset == 0 && endOffset == text.length();
    }

    public static boolean isJavaScript(PsiFile psiFile) {
        return endsWith(psiFile, ".js");
    }

    public static boolean isJava(PsiFile psiFile) {
        return endsWith(psiFile, ".java");
    }

    public static boolean endsWith(PsiFile psiFile, String... suffix) {
        VirtualFile file = psiFile.getVirtualFile();
        for (String s : suffix) {
            if (file.getPath().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static void optimizeImportsByIntellij(PsiFile psiFile) {

        Project project = psiFile.getProject();
        final PsiImportList newImportList = JavaCodeStyleManager.getInstance(project).prepareOptimizeImportsResult(
                (PsiJavaFile) psiFile);

        try {
            final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
            final Document document = manager.getDocument(psiFile);
            if (document != null) {
                manager.commitDocument(document);
            }
            final PsiImportList oldImportList = ((PsiJavaFile) psiFile).getImportList();
            assert oldImportList != null;
            if (newImportList != null) {
                oldImportList.replace(newImportList);
            }
            manager.doPostponedOperationsAndUnblockDocument(document);
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }

    public static Properties readPropertiesFile(File file, Properties defaultConfig) {
        if (!file.exists()) {
            throw new FileDoesNotExistsException(file);

        }
        BufferedInputStream stream = null;
        final Properties formatterOptions;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            formatterOptions = new Properties(defaultConfig);
            formatterOptions.load(stream);
        } catch (IOException e) {
            throw new RuntimeException("config file read error", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
        return formatterOptions;
    }

    public static Properties readPropertiesFile(File file) {
        return readPropertiesFile(file, null);
    }

    public static Properties readXmlJavaSettingsFile(File file, Properties properties, String profile) {
        int defaultSize = properties.size();
        if (!file.exists()) {
            throw new FileDoesNotExistsException(file);
        }
        if (profile == null) {
            throw new IllegalStateException("loading of profile settings failed, selected profile is null");
        }
        boolean profileFound = false;
        try { // load file profiles
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList profiles = doc.getElementsByTagName("profile");
            if (profiles.getLength() == 0) {
                throw new IllegalStateException("loading of profile settings failed, file does not contain any profiles");
            }
            for (int temp = 0; temp < profiles.getLength(); temp++) {
                Node profileNode = profiles.item(temp);
                if (profileNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element profileElement = (Element) profileNode;
                    String name = profileElement.getAttribute("name");
                    String kind = profileElement.getAttribute("kind");
                    if ("CodeFormatterProfile".equals(kind) && profile.equals(name)) {
                        profileFound = true;
                        NodeList childNodes = profileElement.getElementsByTagName("setting");
                        if (childNodes.getLength() == 0) {
                            throw new IllegalStateException("loading of profile settings failed, profile has no settings elements");
                        }
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            Node item = childNodes.item(i);
                            if (item.getNodeType() == Node.ELEMENT_NODE) {
                                Element attributeItem = (Element) item;
                                String id = attributeItem.getAttribute("id");
                                String value = attributeItem.getAttribute("value");
                                properties.setProperty(id.trim(), value.trim());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("file: " + file.getAbsolutePath() + ", profile: " + profile, e);
            throw new InvalidPropertyFile(e.getMessage(), e);
        }
        if (!profileFound) {
            throw new IllegalStateException("profile not found in the file");
        }
        if (properties.size() == defaultSize) {
            throw new IllegalStateException("no properties loaded, something is broken");
        }
        return properties;
    }
}
