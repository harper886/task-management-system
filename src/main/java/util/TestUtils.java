package util;

import javax.swing.*;
import java.awt.*;

public class TestUtils {

    public static Component findComponent(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals(text)) {
                return comp;
            }
            if (comp instanceof Container) {
                Component found = findComponent((Container) comp, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    public static Component findComponent(Container container, Class<?> clazz) {
        for (Component comp : container.getComponents()) {
            if (clazz.isInstance(comp)) {
                return comp;
            }
            if (comp instanceof Container) {
                Component found = findComponent((Container) comp, clazz);
                if (found != null) return found;
            }
        }
        return null;
    }
}