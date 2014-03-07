package com.fydp.smarttouchassistant.utils;

import java.util.ArrayList;
import java.util.List;

import com.fydp.smarttouchassistant.FileExplorerActivity.FILETYPE;

public class FileDirectoryTree<T> {
	private Node<T> root;

	public FileDirectoryTree(T rootData) {
		root = new Node<T>(rootData, FILETYPE.FOLDER, null);
	}

	public Node<T> getRoot() {
		return root;
	}

	public static class Node<T> {
		private T data;
		private Node<T> parent;
		private List<Node<T>> children;
		private FILETYPE dataType;

		public Node(T data, FILETYPE dataType, Node<T> parent) {
			this.data = data;
			this.dataType = dataType;
			this.parent = parent;
		}

		public void insert(T data, FILETYPE dataType) {
			Node<T> node = new Node<T>(data, dataType, this);
			if (children == null) {
				children = new ArrayList<Node<T>>();
			}
			children.add(node);
		}

		public T getData() {
			return data;
		}

		public FILETYPE getDataType() {
			return dataType;
		}

		public Node<T> getParent() {
			return parent;
		}

		public List<Node<T>> getChildren() {
			if (children == null) {
				children = new ArrayList<Node<T>>();
			}
			return children;
		}
	}

}
