package priorityqueue;

public class PriorityQueue<T extends Comparable<T>> {
    private Node<T>[] heap;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public PriorityQueue() {
        // Tworzymy tablicę obiektów Node z typem T
        heap = (Node<T>[]) new Node[INITIAL_CAPACITY];
        size = 0;
    }

    public void add(T element) {
        if (size >= heap.length) {
            resize();
        }
        heap[size] = new Node<>(element);
        heapifyUp(size);
        size++;
    }

    public T poll() {
        if (isEmpty()) {
            return null;
        }
        T min = heap[0].data;
        heap[0] = heap[size - 1];
        size--;
        if (!isEmpty()) {
            heapifyDown(0);
        }
        return min;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap[index].data.compareTo(heap[parentIndex].data) < 0) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int smallest = index;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            if (leftChild < size && heap[leftChild].data.compareTo(heap[smallest].data) < 0) {
                smallest = leftChild;
            }
            if (rightChild < size && heap[rightChild].data.compareTo(heap[smallest].data) < 0) {
                smallest = rightChild;
            }

            if (smallest == index) {
                break;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        Node<T> temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        Node<T>[] newHeap = (Node<T>[]) new Node[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }

    private class Node<T> {
        T data;

        Node(T data) {
            this.data = data;
        }
    }
}
