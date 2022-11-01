package ru.vsu.cs.course1.tree;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;


/*
/ сам класс дерева. Хранит максимальное количество элементов в ноде,
/ минималоьное количество эелментов в ноде, по умолчанию(и в этой реализации
/ стоит DIMENSION=2, root и размер.
 */
public class RTree<T> {

    /*
    / класс для хранения прямоугольников, в которых
    / хранятся все ноды и в которых представлено дерево.
    / создается с двумя кордианатами: нижней левой
    / и верхней правой.
     */
    private static class Rectangle {

        private final float[] minCoords;
        private final float[] maxCoords;

        public Rectangle(float minY, float minX, float maxY, float maxX) {
            minCoords = new float[]{minX, minY};
            maxCoords = new float[]{maxX, maxY};
        }

        public Rectangle(float[] coordinates) {
            minCoords = new float[]{coordinates[0], coordinates[1]};
            maxCoords = new float[]{coordinates[2], coordinates[3]};
        }

        public Rectangle(Rectangle rectangle) {
            minCoords = new float[]{rectangle.getMinX(), rectangle.getMinY()};
            maxCoords = new float[]{rectangle.getMaxX(), rectangle.getMaxY()};
        }

        public Rectangle() {
            minCoords = new float[DIEMENSION];
            maxCoords = new float[DIEMENSION];
        }

        public float calcArea() {
            return (this.getMaxX() - this.getMinX()) * (this.getMaxY() - this.getMinY());
        }

        public boolean isOverlap(Rectangle rect) {
            return (this.getMinX() <= rect.getMinX()
                    && this.getMaxX() >= rect.getMinX()
                    && this.getMinY() <= rect.getMinY()
                    && this.getMaxY() >= rect.getMinY())
                    || (this.getMinX() <= rect.getMaxX()
                    && this.getMaxX() >= rect.getMaxX()
                    && this.getMinY() <= rect.getMaxY()
                    && this.getMaxY() >= rect.getMaxY());
        }

        public float getMaxX() {
            return maxCoords[0];
        }

        public float getMaxY() {
            return maxCoords[1];
        }

        public float getMinX() {
            return minCoords[0];
        }

        public void setAllCoords(Rectangle rectangle) {
            this.setMinX(rectangle.getMinX());
            this.setMaxX(rectangle.getMaxX());
            this.setMinY(rectangle.getMinY());
            this.setMaxY(rectangle.getMaxY());
        }
        public float getMinY() {
            return minCoords[1];
        }
        public void setMaxX(float maxX) {
            maxCoords[0] = maxX;
        }
        public void setMaxY(float maxY) {
            maxCoords[1] = maxY;
        }
        public void setMinX(float minX) {
            minCoords[0] = minX;
        }
        public void setMinY(float minY) {
            minCoords[1] = minY;
        }

        public boolean isRectInside(Rectangle rectangle) {
            return this.getMaxX() >= rectangle.getMaxX()
                    && this.getMinX() <= rectangle.getMinX()
                    && this.getMaxY() >= rectangle.getMaxY()
                    && this.getMinY() <= rectangle.getMinY();
        }

        @Override
        public boolean equals(Object o) {
            Rectangle rectangle = (Rectangle) o;
            return Arrays.equals(minCoords, rectangle.minCoords) && Arrays.equals(maxCoords, rectangle.maxCoords);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(minCoords);
            result = 31 * result + Arrays.hashCode(maxCoords);
            return result;
        }
    }

    /*
    / класс ноды дерева. Хранит в себе прямоугольник,
    / своих детей, булин переменную на лист/не лист,
    / своего родителя, объект, если может хранится.
     */
    private class RTreeNode<T> {

        //        private final float[] coordinates;
        private final Rectangle rectangle;
        private final LinkedList<RTreeNode<T>> childrens;
        private boolean asLeaf;
        private RTreeNode<T> parent;
        private T object;

        public RTreeNode(float[] coordinates, boolean asLeaf) {
            this.parent = null;
            this.rectangle = new Rectangle(coordinates);
//            this.coordinates = coordinates;
            this.childrens = new LinkedList<>();
            this.object = null;
            this.asLeaf = asLeaf;
        }

        private RTreeNode(Rectangle rect, boolean asLeaf) {
            this.parent = null;
            this.rectangle = rect;
//            this.coordinates = coordinates;
            this.childrens = new LinkedList<>();
            this.object = null;
            this.asLeaf = asLeaf;
        }

        public RTreeNode(float[] coordinates, T object, RTreeNode<T> parent) {
            this.parent = parent;
            this.rectangle = new Rectangle(coordinates);
//            this.coordinates = coordinates;
            this.childrens = new LinkedList<>();
            this.asLeaf = true;
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            RTreeNode<?> rTreeNode = (RTreeNode<?>) o;
            if (this.object == null && rTreeNode.object == null) {
                return asLeaf == rTreeNode.asLeaf
                        && rectangle.equals(rTreeNode.rectangle);
            }
            return asLeaf == rTreeNode.asLeaf
                    && rectangle.equals(rTreeNode.rectangle)
                    && object.equals(rTreeNode.object);
        }

        @Override
        public String toString() {
            return "Entry: " + object;
        }

        @Override
        public int hashCode() {
            return Objects.hash(rectangle, asLeaf, object);
        }

    }

    private final int maxEntries;
    private final int minEntries;
    private RTreeNode<T> root;
    private final static int DIEMENSION = 2;
    private int size;

    /*
    / метод для создания корня по умолчанию.
     */
    private RTreeNode<T> buildRoot(boolean asLeaf) {
        return new RTreeNode<T>(new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE}, asLeaf);
    }

    public RTree(int minEntries, int maxEntries) throws Exception {
        if (maxEntries < 4) {
            throw new Exception("Max entries smaller than 4");
        }
        if (minEntries > maxEntries / 2) {
            throw new Exception("Min entries bigger than half of max entries");
        }
        this.minEntries = minEntries;
        this.maxEntries = maxEntries;
        this.root = buildRoot(true);
    }

    /*
    / метод вставки. Принимает координаты вставки и объект.
    / вызывает методы: choosenLeaf, splitNode, adjustTree.
     */
    public void insert(float[] coordinates, T object) throws Exception {
        if (coordinates.length / 2 > DIEMENSION) {
            throw new Exception("Wrong coordinates for 2D");
        }
        RTreeNode<T> entry = new RTreeNode<T>(coordinates, object, null);
        RTreeNode<T> choosenLeaf = chooseLeaf(root, entry);
        choosenLeaf.childrens.add(entry);
        this.size++;
        entry.parent = choosenLeaf;
        if (choosenLeaf.childrens.size() > maxEntries) {
            RTreeNode<T>[] splits = splitNode(choosenLeaf);
            adjustTree(splits[0], splits[1]);
        } else {
            adjustTree(choosenLeaf, null);
        }
    }

    /*
    / принимает две распличенные ноды, или одну,
    / и производит расширение нод, перезапись родителей,
    / добавление детей. Является рекурсивной, вызывает метод
    / tighteen.
     */
    private void adjustTree(RTreeNode<T> firstSplit, RTreeNode<T> secondSplit) {
        if (firstSplit == root) {
            if (secondSplit != null) {
                this.root = buildRoot(false);
                root.childrens.add(firstSplit);
                root.childrens.add(secondSplit);
                firstSplit.parent = root;
                secondSplit.parent = root;
            }
            tighten(root);
            return;
        }
        tighten(firstSplit);
        if (secondSplit != null) {
            tighten(secondSplit);
            if (firstSplit.parent.childrens.size() > maxEntries) {
                RTreeNode<T>[] splits = splitNode(firstSplit.parent);
                adjustTree(splits[0], splits[1]);
            }
        }
        if (firstSplit.parent != null) {
            adjustTree(firstSplit.parent, null);
        }
    }

    /*
    / метод разделения нод. Основан на линейной разделении.
    / Принимает ноду которую нужно разделить.
    / Создает массив новых нож, ищет линейным разделением ноды, которые подходят
    / лучше всего, перезаписывает массив найденными нодами,
    / в цикле, пока все дети не будут распределены, делает проверку
    / на соответсвие разделенным нодам на минимальное количество в них детей
    / по условию MinEntries дерева.
    / Разделяет ноды таким образом, чтобы для установленных в массивах нодах
    / нужно было меньше всего увеличивать площадь, для вставки туда новы в качестве
    / ребенка. После проделанных манипуляций для нод вызывается thighteen для перерасчета
    / площадей.
     */
    private RTreeNode<T>[] splitNode(RTreeNode<T> node) {
        RTreeNode<T>[] newNodes = new RTreeNode[]{node, new RTreeNode<T>(new Rectangle(node.rectangle), node.asLeaf)};
        newNodes[1].parent = node.parent;
        if (newNodes[1].parent != null) {
            newNodes[1].parent.childrens.add(newNodes[1]);
        }
        LinkedList<RTreeNode<T>> currChildrens = new LinkedList<>(node.childrens);
        node.childrens.clear();
        RTreeNode<T>[] maxLeftMaxRight = linearSplit(currChildrens);
        newNodes[0].childrens.add(maxLeftMaxRight[0]);
        newNodes[0].rectangle.setAllCoords(maxLeftMaxRight[0].rectangle);
        newNodes[1].childrens.add(maxLeftMaxRight[1]);
        newNodes[1].rectangle.setAllCoords(maxLeftMaxRight[1].rectangle);
        while (!currChildrens.isEmpty()) {
            if ((newNodes[0].childrens.size() >= minEntries)
                && (newNodes[1].childrens.size() + currChildrens.size() == minEntries)) {
                newNodes[1].childrens.addAll(currChildrens);
                currChildrens.clear();
            } else if ((newNodes[1].childrens.size() >= minEntries)
                    && (newNodes[0].childrens.size() + currChildrens.size() == minEntries)) {
                newNodes[0].childrens.addAll(currChildrens);
                currChildrens.clear();
            }
            if (currChildrens.isEmpty()) {
                tighten(newNodes);
                return newNodes;
            }
            RTreeNode<T> children = currChildrens.pop();
            float firstIncreasedArea = calcIncreasedArea(newNodes[0], children);
            float secondIncreasedArea = calcIncreasedArea(newNodes[1], children);
            RTreeNode<T> prefNode;
            if (firstIncreasedArea < secondIncreasedArea) {
                prefNode = newNodes[0];
            } else if (firstIncreasedArea > secondIncreasedArea) {
                prefNode = newNodes[1];
            } else {
                float firstArea = newNodes[0].rectangle.calcArea();
                float secondArea = newNodes[1].rectangle.calcArea();
                if (firstArea < secondArea) {
                    prefNode = newNodes[0];
                } else if (firstArea > secondArea) {
                    prefNode = newNodes[1];
                } else {
                    prefNode = newNodes[(int) (Math.random() * 2)];
                }
            }
            prefNode.childrens.add(children);
            tighten(prefNode);
        }
        return newNodes;
    }

    /*
    / метод линейного разделения. В цикле ищет максимальную нижнюю границу прямоугольника
    / и минимальную верхнюю, максимальную и минимальную координату среди всех точек,
    / запоминаются ноды и значения. Потом берется нормализованная величина
    / из разницы выше запоминаемых значений. Если величина больше 0, то
    / запоминаются значения нод в цикле, если нет, то берутся просто две первые ноды.
     */
    private RTreeNode<T>[] linearSplit(LinkedList<RTreeNode<T>> nodes) {
        RTreeNode<T>[] maxLeftMaxRight = new RTreeNode[2];
        float minLow = Float.MAX_VALUE;
        float maxLow = -Float.MAX_VALUE;
        float minHigh = Float.MAX_VALUE;
        float maxHigh = -Float.MAX_VALUE;
        RTreeNode<T> nodeMaxLower = null;
        RTreeNode<T> nodeMinUpper = null;
        for (RTreeNode<T> n : nodes) {
            minLow = Math.min(minLow, n.rectangle.getMinX());
            maxHigh = Math.max(maxHigh, n.rectangle.getMaxX());
            if (n.rectangle.getMinX() > maxLow) {
                maxLow = n.rectangle.getMinX();
                nodeMaxLower = n;
            }
            if (n.rectangle.getMaxX() < minHigh) {
                minHigh = n.rectangle.getMaxX();
                nodeMinUpper = n;
            }
        }
        float normalize = (nodeMaxLower == nodeMinUpper) ? -1.0f :
                (Math.abs(minHigh - maxLow) / (maxHigh - minLow));
        if (normalize <= 0.0f) {
            nodeMinUpper = nodes.get(0);
            nodeMaxLower = nodes.get(1);
        }
        maxLeftMaxRight[0] = nodeMinUpper;
        maxLeftMaxRight[1] = nodeMaxLower;
        nodes.remove(nodeMaxLower);
        nodes.remove(nodeMinUpper);
        return maxLeftMaxRight;
    }

    /*
    / функция для изменения размеров прямоуглльника передаваемых нод
    / в рамках размеоа их детей.
     */
    private void tighten(RTreeNode<T>... nodes) {
        for (RTreeNode<T> node : nodes) {
            float maxX = -Float.MAX_VALUE;
            float minX = Float.MAX_VALUE;
            float maxY = -Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            for (RTreeNode<T> curr : node.childrens) {
                curr.parent = node;
                maxX = Float.max(maxX, curr.rectangle.getMaxX());
                maxY = Float.max(maxY, curr.rectangle.getMaxY());
                minX = Float.min(minX, curr.rectangle.getMinX());
                minY = Float.min(minY, curr.rectangle.getMinY());
            }
            node.rectangle.setMinX(minX);
            node.rectangle.setMaxX(maxX);
            node.rectangle.setMinY(minY);
            node.rectangle.setMaxY(maxY);
        }
    }

    /*
    / функция нахождения подходящего листа для вставки туда ребенка.
    / в рамках цикла вызывается calcIncreasedArea, которая нужна для
    / определения максимально подходящей ноды. Является рекурсивной.
     */
    private RTreeNode<T> chooseLeaf(RTreeNode<T> node, RTreeNode<T> entry) {
        if (node.asLeaf) {
            return node;
        }
        float minIncrease = Float.MAX_VALUE;
        RTreeNode<T> next = null;
        for (RTreeNode<T> child : node.childrens) {
            float increase = calcIncreasedArea(child, entry);
            if (increase < minIncrease) {
                minIncrease = increase;
                next = child;
            } else if (increase == minIncrease) {
                if (child.rectangle.calcArea() < next.rectangle.calcArea()) {
                    next = child;
                }
            }
        }
        return chooseLeaf(next, entry);
    }

    /*
    / функция подсчета увелечения нужной площади.
     */
    private float calcIncreasedArea(RTreeNode<T> node, RTreeNode<T> entry) {
        float lenght = Math.max(node.rectangle.getMaxX(), entry.rectangle.getMaxX())
                - Math.min(node.rectangle.getMinX(), entry.rectangle.getMinX());
        float height = Math.max(node.rectangle.getMaxY(), entry.rectangle.getMaxY())
                - Math.min(node.rectangle.getMinY(), entry.rectangle.getMinY());
        return lenght * height - node.rectangle.calcArea();
    }

    /*
    / функция удаления (УДАЛЯЕТ ПОДХОДЯЩИЙ ЭЛЕМЕНТ ЕСЛИ ОН ХОТЯ БЫ ПЕРЕСЕКАЕТСЯ
    / С ЗАДАННЫМ ПРЯМОУГОЛЬНИКОМ).
    / Сначала спускается к листку, ищет нужную для удаления ноду,
    / удаляет ее. Вызывает функцию condenseTree для изменения дерева при удалении.
     */
    public boolean delete(float[] coordinate, T object) throws Exception {
        RTreeNode<T> leaf = findLeaf(root, new Rectangle(coordinate), object);
        if (leaf == null) {
            throw new RuntimeException("wrong object to delete");
        }
        T removed = null;
        RTreeNode<T> delNode = new RTreeNode<>(coordinate, object, leaf.parent);
        for (RTreeNode<T> child : leaf.parent.childrens) {
            if (child.equals(delNode)) {
                removed = child.object;
                leaf.parent.childrens.remove(child);
                break;
            }
        }
        if (removed != null) {
            this.size--;
        }
        if (this.size == 0) {
            buildRoot(true);
        }
        condenseTree(leaf.parent);
        return true;
    }

    /*
    / находит подходящий лист, в котором лежит нужных объект.
     */
    private RTreeNode<T> findLeaf(RTreeNode<T> node, Rectangle rect, T object) {
        if (node.asLeaf) {
            for (RTreeNode<T> child : node.childrens) {
                if (child.object.equals(object)) {
                    return child;
                }
            }
        } else {
            for (RTreeNode<T> child : node.childrens) {
                if (child.rectangle.isOverlap(rect)) {
                    RTreeNode<T> leaf = findLeaf(child, rect, object);
                    if (leaf != null) {
                        return leaf;
                    }
                }
            }
        }
        return null;
    }

    /*
    / функция для изменения дерева. В множество записываются
    / все удаленные дети. Цикл идет от листа к корню.
    / Если в каком-то узле оказалось количество детей меньше
    / MinEntries, то этот узел удаляется, а его дети добавляются в множество.
    / После происходит проверка на то, что осталось, может вызваться либо buildroot,
    / либо выставиться отсавшееся значения для корня. Иначе просто вызывается функция вставки
    / в дерево.
     */
    private void condenseTree(RTreeNode<T> childrens) throws Exception {
        RTreeNode<T> node = childrens;
        Set<RTreeNode<T>> q = new HashSet<>();
        while (node != root) {
            if (node.asLeaf && (node.childrens.size() < minEntries)) {
                q.addAll(node.childrens);
                node.parent.childrens.remove(node);
            } else if (!node.asLeaf && (node.childrens.size() < minEntries)) {
                LinkedList<RTreeNode<T>> toVisit = new LinkedList<>(node.childrens);
                while (!toVisit.isEmpty()) {
                    RTreeNode<T> child = toVisit.pop();
                    if (child.asLeaf) {
                        q.addAll(child.childrens);
                    } else {
                        toVisit.addAll(child.childrens);
                    }
                }
                node.parent.childrens.remove(node);
            } else {
                tighten(node);
            }
            node = node.parent;
        }
        if (root.childrens.size() == 0) {
            root = buildRoot(true);
        } else if ((root.childrens.size() == 1) && (!root.asLeaf)) {
            root = root.childrens.get(0);
            root.parent = null;
        } else {
            tighten(root);
        }
        for (RTreeNode<T> ne : q) {
            insert(new float[]{ne.rectangle.getMinX(), ne.rectangle.getMinY(),
                    ne.rectangle.getMaxX(), ne.rectangle.getMaxY()}, ne.object);
        }
        size -= q.size();
    }
    private void search(Rectangle rect, RTreeNode<T> currentRoot, ArrayList<T> results) {
        if (currentRoot.asLeaf) {
            for (RTreeNode<T> entry : currentRoot.childrens) {
                if (entry.rectangle.isOverlap(rect) || rect.isOverlap(entry.rectangle)) {
                    results.add(entry.object);
                }
            }
        } else {
            for (RTreeNode<T> entry : currentRoot.childrens) {
                if (entry.rectangle.isOverlap(rect) || rect.isOverlap(entry.rectangle)) {
                    search(rect, entry, results);
                }
            }
        }
    }


    /*
    / простая фунция поиска. Принимает коордианты и возвращает
    / все значения, пересекаемые с заданным прямоугольников.
     */
    public List<T> search(float[] coordinates) {
        ArrayList<T> result = new ArrayList<>();
        Rectangle rect = new Rectangle(coordinates);
        search(rect, root, result);
        return result;
    }

    private static final int ELEM_WIDTH = 150;
    private static final int ELEM_HEIGHT = 120;


    public void visualizeTree() {
        PrintStream out = System.out;
        RTreeNode<T> currR = this.root;
        int spacers = 0;
        int childernCount = 0;
        while (true) {
            if (!currR.asLeaf && currR.object == null) {
                spacers += 3;
                currR = currR.childrens.get(0);
            } else if (currR.asLeaf && currR.object == null) {
                spacers += currR.childrens.size() * 3;
            }
            break;
        }
        while (true) {
            if (!currR.asLeaf && currR.object == null) {
                for (int indexChildrens = 0; indexChildrens < currR.childrens.size(); indexChildrens++) {
                    out.print("null  ");
                }
            }
        }
    }
    public String visualize() {
        int ubDepth = (int) Math.ceil(Math.log(size) / Math.log(minEntries)) * ELEM_HEIGHT;
        int ubWidth = size * ELEM_WIDTH;
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        pw.println("<html><head></head><body>");
        visualize(root, pw, 0, 0, ubWidth, ubDepth);
        pw.println("</body>");
        pw.flush();
        try (PrintWriter out = new PrintWriter("src/resources/tree.html")) {
            out.println(sw);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    private void visualize(final RTreeNode<T> n, final java.io.PrintWriter pw, final int x0,
                           final int y0, final int w, final int h) {
        pw.printf("<div style=\"position:absolute; left: %d; top: %d; width: %d; height: %d; border: 1px dashed\">%n",
                x0, y0, w, h);
        pw.println("<pre>");
//        pw.println("Node: " + n.toString() + " (root==" + (n == root) + ") \n");
        pw.println("Node: " + n.toString() + "\n");
        pw.println("Coords: " + Arrays.toString(n.rectangle.minCoords) + ", "
                + Arrays.toString(n.rectangle.maxCoords) + "\n");
        pw.println("# Children: " + ((n.childrens == null) ? 0 : n.childrens.size()) + "\n");
        pw.println("isLeaf: " + n.asLeaf + "\n");
        pw.println("</pre>");
        int numChildren = (n.childrens == null) ? 0 : n.childrens.size();
        for (int i = 0; i < numChildren; i++) {
            visualize(n.childrens.get(i), pw, (int) (x0 + (i * w / (float) numChildren)),
                    y0 + ELEM_HEIGHT, (int) (w / (float) numChildren), h - ELEM_HEIGHT);
        }
        pw.println("</div>");
    }
}
