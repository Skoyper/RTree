package ru.vsu.cs.course1.tree.demo;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import ru.vsu.cs.course1.tree.*;
import ru.vsu.cs.course1.tree.bst.BSTree;
import ru.vsu.cs.course1.tree.bst.SimpleBSTreeMap;
import ru.vsu.cs.course1.tree.bst.avl.AVLTreeMap;
import ru.vsu.cs.course1.tree.bst.rb.RBTreeMap;
import ru.vsu.cs.util.ArrayUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeDemoFrame extends JFrame {

    private class SwingTester {
        private void createWindow() {
            JFrame frame = new JFrame("Swing Tester");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            createUI(frame);
            frame.setSize(560, 450);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }

        private void createUI(final JFrame frame) {
            JPanel panel = new JPanel();
            LayoutManager layout = new FlowLayout();
            panel.setLayout(layout);

            JEditorPane jEditorPane = new JEditorPane();
            jEditorPane.setEditable(false);
            URL url = SwingTester.class.getResource("tree.html");

            try {
                jEditorPane.setPage(url);
            } catch (IOException e) {
                jEditorPane.setContentType("text/html");
                jEditorPane.setText("<html>Page not found.</html>");
            }

            JScrollPane jScrollPane = new JScrollPane(jEditorPane);
            jScrollPane.setPreferredSize(new Dimension(540, 400));

            panel.add(jScrollPane);
            frame.getContentPane().add(panel, BorderLayout.CENTER);
        }
    }
    private JPanel panelMain;
    private JButton buttonPreOrderTraverse;
    private JButton buttonInOrderTraverse;
    private JButton buttonPostOrderTraverse;
    private JButton buttonByLevelTraverse;
    private JTextArea textAreaSystemOut;
    private JButton buttonMakeTree;
    private JButton buttonMakeBSTree;
    private JSplitPane splitPaneMain;
    private JTextField textFieldValues;
    private JSpinner spinnerRandomCount;
    private JButton buttonRandomGenerate;
    private JButton buttonSortValues;
    private JButton buttonMakeBSTree2;
    private JButton buttonMakeAVLTree;
    private JButton buttonMakeRBTree;
    private JTextField textFieldSingleValue;
    private JButton buttonAddValue;
    private JButton buttonRemoveValue;
    private JPanel panelPaintArea;
    private JSpinner spinnerSingleValue;
    private JButton bottonFindMinValue;
    private JTextField textMinEntries;
    private JTextField textMaxEntries;
    private JTextField textMinX;
    private JTextField textMaxX;
    private JTextField textMinY;
    private JTextField textMaxY;
    private JScrollPane showTree;
    private JEditorPane editorPane;
    private JTextArea textArea1;

    private JMenuBar menuBarMain;
    private JPanel paintPanel = null;
    private JFileChooser fileChooserSave;

    BinaryTree<Integer> tree = new SimpleBinaryTree<>();

    BinaryTree<Float> floatTree = new SimpleBinaryTree<>();

    RTree<Integer> floatTree1 = new RTree<>(2, 4);

    private SwingTester st = new SwingTester();


    public TreeDemoFrame() throws Exception {
        this.setTitle("Двоичные деревья");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        createMenu();

        splitPaneMain.setDividerLocation(0.5);
        splitPaneMain.setResizeWeight(1.0);
        splitPaneMain.setBorder(null);

        paintPanel = new JPanel() {
            private Dimension paintSize = new Dimension(0, 0);

            @Override
            public void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                paintSize = BinaryTreePainter.paint(floatTree, gr);
                if (!paintSize.equals(this.getPreferredSize())) {
                    SwingUtils.setFixedSize(this, paintSize.width, paintSize.height);
                }
            }
        };
        JScrollPane paintJScrollPane = new JScrollPane(paintPanel);
        panelPaintArea.add(paintJScrollPane);

        fileChooserSave = new JFileChooser();
        fileChooserSave.setCurrentDirectory(new File("./images"));
        FileFilter filter = new FileNameExtensionFilter("SVG images", "svg");
        fileChooserSave.addChoosableFileFilter(filter);
        fileChooserSave.setAcceptAllFileFilterUsed(false);
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setApproveButtonText("Save");

        buttonMakeTree.addActionListener(actionEvent -> {
            try {
                Integer min = Integer.parseInt(textMinEntries.getText());
                Integer max = Integer.parseInt(textMaxEntries.getText());
                RTree<Integer> tree = new RTree<>(min, max);
                this.floatTree1 = tree;
                showTree();
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });

        bottonFindMinValue.addActionListener(actionEvent -> {
            try {
                float[] coords = new float[]{Float.parseFloat(textMinX.getText()),
                        Float.parseFloat(textMinY.getText()),
                        Float.parseFloat(textMaxX.getText()),
                        Float.parseFloat(textMaxY.getText())};
                List<Integer> s = floatTree1.search(coords);
                textArea1.setText(s.toString());
                showTree();
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });

        buttonAddValue.addActionListener(actionEvent -> {
            if (!(floatTree1 instanceof RTree<?>)) {
                SwingUtils.showInfoMessageBox("Текущее дерево не является деревом поиска!");
                return;
            }
            try {
                int value = Integer.parseInt(spinnerSingleValue.getValue().toString());
                float[] coords = new float[]{Float.parseFloat(textMinX.getText()),
                        Float.parseFloat(textMinY.getText()),
                        Float.parseFloat(textMaxX.getText()),
                        Float.parseFloat(textMaxY.getText())};
                ((RTree<Integer>) floatTree1).insert(coords, value);
                showTree();
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });
        buttonRemoveValue.addActionListener(actionEvent -> {
            try {
                int value = Integer.parseInt(spinnerSingleValue.getValue().toString());
                float[] coords = new float[]{Float.parseFloat(textMinX.getText()),
                        Float.parseFloat(textMinY.getText()),
                        Float.parseFloat(textMaxX.getText()),
                        Float.parseFloat(textMaxY.getText())};
                floatTree1.delete(coords, value);
                showTree();
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });
    }

    private void showTree() {
        floatTree1.visualize();
        Path rootDir = Paths.get(".").normalize().toAbsolutePath();
        File file = new File(rootDir.toString() + "/src/resources/tree.html");
        URL url;
        try {
            editorPane = new JEditorPane();
            url = file.toURI().toURL();
            editorPane.setPage(url);
        } catch (IOException e) {
            editorPane.setContentType("text/html");
            editorPane.setText("<html>Page not found.</html>");
        }
        editorPane.updateUI();
        showTree.setViewportView(editorPane);
        showTree.updateUI();
//        showTree.setViewportView(jEditorPane);
    }

        /**
         * Создание меню
         */
        private void createMenu () {
            JMenu menuTesting = new JMenu("Тестирование");
            Class[] mapClasses = {SimpleBSTreeMap.class, AVLTreeMap.class, RBTreeMap.class};
            for (Class mapClass : mapClasses) {
                JMenuItem menuItem = new JMenuItem("Корректность " + mapClass.getSimpleName());
                menuItem.addActionListener(actionEvent -> {
                    try {
                        Map<Integer, Integer> map = (Map<Integer, Integer>) mapClass.getConstructor().newInstance();
                        showSystemOut(() -> {
                            MapTest.testCorrect(map);
                        });
                    } catch (Exception e) {
                        SwingUtils.showErrorMessageBox(e);
                    }
                });
                menuTesting.add(menuItem);
            }

            menuBarMain = new JMenuBar();
            menuBarMain.add(menuTesting);
            setJMenuBar(menuBarMain);
        }

        /**
         * Перерисовка дерева
         */
        public void repaintTree () {
            //panelPaintArea.repaint();
            paintPanel.repaint();
            //panelPaintArea.revalidate();
        }

        /**
         * Выполнение действия с выводом стандартного вывода в окне (textAreaSystemOut)
         *
         * @param action Выполняемое действие
         */
        private void showSystemOut (Runnable action){
            PrintStream oldOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(baos, true, "UTF-8"));

                action.run();

                textAreaSystemOut.setText(baos.toString("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                SwingUtils.showErrorMessageBox(e);
            }
            System.setOut(oldOut);
        }

        /**
         * Заполнить дерево добавлением всех элементов (textFieldValues)
         *
         * @param tree Дерево
         */
        private void makeBSTFromValues (BSTree < Integer > tree) {
            int[] values = ArrayUtils.toIntArray(textFieldValues.getText());
            tree.clear();
            for (int v : values) {
                tree.put(v);
            }
            this.tree = tree;
            repaintTree();
        }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), 10, 10));
        splitPaneMain = new JSplitPane();
        panelMain.add(splitPaneMain, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPaneMain.setLeftComponent(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Дерево в скобочной нотации:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonMakeTree = new JButton();
        buttonMakeTree.setText("Построить дерево");
        panel2.add(buttonMakeTree, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonMakeBSTree = new JButton();
        buttonMakeBSTree.setText("Построить дерево поиска");
        panel2.add(buttonMakeBSTree, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottonFindMinValue = new JButton();
        bottonFindMinValue.setText("Найти минимальное значение");
        panel2.add(bottonFindMinValue, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textFieldValues = new JTextField();
        textFieldValues.setText("6, 8, 3, 5, 7, 2, 16, 1, 15, 12, 9");
        panel3.add(textFieldValues, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spinnerRandomCount = new JSpinner();
        panel4.add(spinnerRandomCount, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, -1), new Dimension(80, -1), new Dimension(80, -1), 0, false));
        buttonRandomGenerate = new JButton();
        buttonRandomGenerate.setText("Сгенерировать");
        panel4.add(buttonRandomGenerate, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonSortValues = new JButton();
        buttonSortValues.setText("Упорядочить");
        panel4.add(buttonSortValues, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("cлучайных чисел");
        panel4.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonMakeBSTree2 = new JButton();
        buttonMakeBSTree2.setText("Построить дерево поиска");
        panel5.add(buttonMakeBSTree2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonMakeAVLTree = new JButton();
        buttonMakeAVLTree.setText("Построить АВЛ-дерево");
        panel5.add(buttonMakeAVLTree, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonMakeRBTree = new JButton();
        buttonMakeRBTree.setText("Построить красно-черное дерево");
        panel5.add(buttonMakeRBTree, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel6.add(spacer3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonAddValue = new JButton();
        buttonAddValue.setText("Добавить");
        panel6.add(buttonAddValue, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonRemoveValue = new JButton();
        buttonRemoveValue.setText("Удалить");
        panel6.add(buttonRemoveValue, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spinnerSingleValue = new JSpinner();
        panel6.add(spinnerSingleValue, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, -1), new Dimension(80, -1), new Dimension(80, -1), 0, false));
        panelPaintArea = new JPanel();
        panelPaintArea.setLayout(new BorderLayout(0, 0));
        panel1.add(panelPaintArea, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panelPaintArea.add(spacer4, BorderLayout.CENTER);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel7.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPaneMain.setRightComponent(panel8);
        buttonPreOrderTraverse = new JButton();
        buttonPreOrderTraverse.setText("Прямой обход");
        panel8.add(buttonPreOrderTraverse, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonInOrderTraverse = new JButton();
        buttonInOrderTraverse.setText("Симметричный обход");
        panel8.add(buttonInOrderTraverse, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonPostOrderTraverse = new JButton();
        buttonPostOrderTraverse.setText("Обратный обход");
        panel8.add(buttonPostOrderTraverse, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonByLevelTraverse = new JButton();
        buttonByLevelTraverse.setText("Обход в ширину");
        panel8.add(buttonByLevelTraverse, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel8.add(scrollPane1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textAreaSystemOut = new JTextArea();
        scrollPane1.setViewportView(textAreaSystemOut);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }
}
