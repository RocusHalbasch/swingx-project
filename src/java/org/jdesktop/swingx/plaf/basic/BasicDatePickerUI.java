package org.jdesktop.swingx.plaf.basic;

import org.jdesktop.swingx.plaf.DatePickerUI;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXDatePickerFormatter;
import org.jdesktop.swingx.calendar.DateSpan;
import org.jdesktop.swingx.calendar.JXMonthView;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.event.*;
import java.awt.*;
import java.util.Date;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Joshua Outwater
 */
public class BasicDatePickerUI extends DatePickerUI {
    protected JXDatePicker datePicker;
    private JButton popupButton;
    private BasicDatePickerPopup popup;
    private Handler handler;

    public static ComponentUI createUI(JComponent c) {
        return new BasicDatePickerUI();
    }

    public void installUI(JComponent c) {
        datePicker = (JXDatePicker)c;
        installComponents();
        installDefaults();
        installKeyboardActions();
        installListeners();
    }

    public void uninstallUI(JComponent c) {
        uninstallListeners();
        uninstallKeyboardActions();
        uninstallDefaults();
        uninstallComponents();
        datePicker = null;
    }

    protected void installComponents() {
        JFormattedTextField editor = datePicker.getEditor();
        if (editor == null || editor instanceof UIResource) {
            datePicker.setEditor(createEditor());
        }
        datePicker.add(datePicker.getEditor());

        popupButton = createPopupButton();

        if (popupButton != null) {
            // this is a trick to get hold of the client prop which
            // prevents closing of the popup
            JComboBox box = new JComboBox();
            Object preventHide = box.getClientProperty("doNotCancelPopup");
            popupButton.putClientProperty("doNotCancelPopup", preventHide);
            datePicker.add(popupButton);
        }
    }

    protected void uninstallComponents() {
        datePicker.remove(datePicker.getEditor());

        if (popupButton != null) {
            datePicker.remove(popupButton);
            popupButton = null;
        }
    }

    protected void installDefaults() {

    }

    protected void uninstallDefaults() {

    }

    protected void installKeyboardActions() {
        KeyStroke enterKey =
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

        JFormattedTextField editor = datePicker.getEditor();
        InputMap inputMap = editor.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(enterKey, "COMMIT_EDIT");

        ActionMap actionMap = editor.getActionMap();
        actionMap.put("COMMIT_EDIT", new CommitEditAction());

        KeyStroke spaceKey =
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false);

        inputMap = popupButton.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(spaceKey, "TOGGLE_POPUP");

        actionMap = popupButton.getActionMap();
        actionMap.put("TOGGLE_POPUP", new TogglePopupAction());

    }

    protected void uninstallKeyboardActions() {

    }

    protected void installListeners() {
        handler = new Handler();

        datePicker.addPropertyChangeListener(handler);

        if (popupButton != null) {
            popupButton.addPropertyChangeListener(handler);
            popupButton.addMouseListener(handler);
            popupButton.addMouseMotionListener(handler);
        }

    }

    protected void uninstallListeners() {
        datePicker.removePropertyChangeListener(handler);

        if (popupButton != null) {
            popupButton.removePropertyChangeListener(handler);
            popupButton.removeMouseListener(handler);
            popupButton.removeMouseMotionListener(handler);
        }

        handler = null;
    }

    /**
     * Creates the editor used to edit the date selection.  Subclasses should
     * override this method if they want to substitute in their own editor.
     *
     * @return an instance of a JFormattedTextField
     */
    protected JFormattedTextField createEditor() {
        JFormattedTextField f = new DefaultEditor(new JXDatePickerFormatter());
        f.setName("dateField");
        f.setColumns(UIManager.getInt("JXDatePicker.numColumns"));
        f.setBorder(UIManager.getBorder("JXDatePicker.border"));

        return f;
    }

    protected JButton createPopupButton() {
        JButton b = new JButton();
        b.setName("popupButton");
        b.setRolloverEnabled(false);

        Icon icon = UIManager.getIcon("JXDatePicker.arrowDown.image");
        if (icon == null) {
            icon = (Icon)UIManager.get("Tree.expandedIcon");
        }
        b.setIcon(icon);

        return b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension dim = datePicker.getEditor().getPreferredSize();
        if (popupButton != null) {
            dim.width += popupButton.getPreferredSize().width;
        }
        Insets insets = datePicker.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return (Dimension)dim.clone();
    }

    public void doLayout() {
        int width = datePicker.getWidth();
        int height = datePicker.getHeight();

        int popupButtonWidth = popupButton != null ? popupButton.getPreferredSize().width : 0;

        Insets insets = datePicker.getInsets();
        datePicker.getEditor().setBounds(insets.left,
                insets.bottom,
                width - popupButtonWidth,
                height);
        if (popupButton != null) {
            popupButton.setBounds(width - popupButtonWidth + insets.left,
                    insets.bottom,
                    popupButtonWidth,
                    height);
        }
    }

    /**
     * Action used to commit the current value in the JFormattedTextField.
     * This action is used by the keyboard bindings.
     */
    private class CommitEditAction extends AbstractAction {
        public CommitEditAction() {
            super("CommitEditPopup");
        }

        public void actionPerformed(ActionEvent ev) {
            try {
                JFormattedTextField editor = datePicker.getEditor();
                // Commit the current value.
                editor.commitEdit();

                // Reformat the value according to the formatter.
                editor.setValue(editor.getValue());
                datePicker.postActionEvent();
            } catch (java.text.ParseException ex) {
            }
        }
    }

    /**
     * Action used to commit the current value in the JFormattedTextField.
     * This action is used by the keyboard bindings.
     */
    private class TogglePopupAction extends AbstractAction {
        public TogglePopupAction() {
            super("TogglePopup");
        }

        public void actionPerformed(ActionEvent ev) {
            handler.toggleShowPopup();
        }
    }

    private class DefaultEditor extends JFormattedTextField implements UIResource {
        public DefaultEditor(AbstractFormatter formatter) {
            super(formatter);
        }
    }

    /**
     * Popup component that shows a JXMonthView component along with controlling
     * buttons to allow traversal of the months.  Upon selection of a date the
     * popup will automatically hide itself and enter the selection into the
     * editable field of the JXDatePicker.
     */
    protected class BasicDatePickerPopup extends JPopupMenu
            implements ActionListener {

        public BasicDatePickerPopup() {
            JXMonthView monthView = datePicker.getMonthView();
            monthView.setActionCommand("MONTH_VIEW");
            monthView.addActionListener(this);

            setLayout(new BorderLayout());
            add(monthView, BorderLayout.CENTER);
            JPanel linkPanel = datePicker.getLinkPanel();
            if (linkPanel != null) {
                add(linkPanel, BorderLayout.SOUTH);
            }
        }

        public void actionPerformed(ActionEvent ev) {
            String command = ev.getActionCommand();
            if ("MONTH_VIEW".equals(command)) {
                DateSpan span = datePicker.getMonthView().getSelectedDateSpan();
                datePicker.getEditor().setValue(span.getStartAsDate());
                setVisible(false);
                datePicker.postActionEvent();
            }
        }
    }


    private class Handler implements MouseListener, MouseMotionListener, PropertyChangeListener {
        private boolean _forwardReleaseEvent = false;

        public void mouseClicked(MouseEvent ev) {
        }

        public void mousePressed(MouseEvent ev) {
            if (!datePicker.isEnabled()) {
                return;
            }

            if (!datePicker.isEditable()) {
                JFormattedTextField editor = datePicker.getEditor();
                if (editor.isEditValid()) {
                    try {
                        editor.commitEdit();
                    } catch (java.text.ParseException ex) {
                    }
                }
            }
            toggleShowPopup();
        }

        public void mouseReleased(MouseEvent ev) {
            if (!datePicker.isEnabled() || !datePicker.isEditable()) {
                return;
            }

            // Retarget mouse event to the month view.
            if (_forwardReleaseEvent) {
                JXMonthView monthView = datePicker.getMonthView();
                ev = SwingUtilities.convertMouseEvent(popupButton, ev,
                        monthView);
                monthView.dispatchEvent(ev);
                _forwardReleaseEvent = false;
            }
        }

        public void mouseEntered(MouseEvent ev) {
        }

        public void mouseExited(MouseEvent ev) {
        }

        public void mouseDragged(MouseEvent ev) {
            if (!datePicker.isEnabled() || !datePicker.isEditable()) {
                return;
            }

            _forwardReleaseEvent = true;

            if (!popup.isShowing()) {
                return;
            }

            // Retarget mouse event to the month view.
            JXMonthView monthView = datePicker.getMonthView();
            ev = SwingUtilities.convertMouseEvent(popupButton, ev, monthView);
            monthView.dispatchEvent(ev);
        }

        public void mouseMoved(MouseEvent ev) {
        }

        public void toggleShowPopup() {
            if (popup == null) {
                popup = new BasicDatePickerPopup();
            }
            if (!popup.isVisible()) {
                JFormattedTextField editor = datePicker.getEditor();
                if (editor.getValue() == null) {
                    editor.setValue(new Date(datePicker.getLinkDate()));
                }
                DateSpan span =
                        new DateSpan((java.util.Date)editor.getValue(),
                                (java.util.Date)editor.getValue());
                JXMonthView monthView = datePicker.getMonthView();
                monthView.setSelectedDateSpan(span);
                monthView.ensureDateVisible(
                        ((Date)editor.getValue()).getTime());
                popup.show(datePicker,
                        0, datePicker.getHeight());
            } else {
                popup.setVisible(false);
            }
        }

        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            if ("enabled".equals(property)) {
                boolean isEnabled = datePicker.isEnabled();
                popupButton.setEnabled(isEnabled);
                datePicker.getEditor().setEnabled(isEnabled);
            } else if ("editable".equals(property)) {
                boolean isEditable = datePicker.isEditable();
                datePicker.getMonthView().setEnabled(isEditable);
                datePicker.getEditor().setEditable(isEditable);
            } else if (JComponent.TOOL_TIP_TEXT_KEY.equals(property)) {
                String tip = datePicker.getToolTipText();
                datePicker.getEditor().setToolTipText(tip);
                popupButton.setToolTipText(tip);
            } else if (JXDatePicker.MONTH_VIEW.equals(property)) {
                popup = null;
            } else if (JXDatePicker.LINK_PANEL.equals(property)) {
                // If the popup is null we haven't shown it yet.
                JPanel linkPanel = datePicker.getLinkPanel();
                if (popup != null) {
                    popup.remove(linkPanel);
                    popup.add(linkPanel, BorderLayout.SOUTH);
                }
            } else if (JXDatePicker.EDITOR.equals(property)) {
                JFormattedTextField oldEditor = (JFormattedTextField)e.getOldValue();
                if (oldEditor != null) {
                    datePicker.remove(oldEditor);
                }

                JFormattedTextField editor = (JFormattedTextField)e.getNewValue();
                datePicker.add(editor);
                datePicker.revalidate();
            }
        }
    }
}
