package com.fydp.smarttouchassistant.utils;

import java.util.ArrayList;
import java.util.List;

import com.fydp.smarttouchassistant.FileExplorerActivity.FILETYPE;

public class FileDirectoryTree {
	private Node root;

	public FileDirectoryTree(String rootData) {
		root = new Node(rootData, FILETYPE.FOLDER, null);
	}

	public Node getRoot() {
		return root;
	}

	public static class Node {
		private String data;
		private Node parent;
		private List<Node> children;
		private FILETYPE dataType;

		public Node(String data, FILETYPE dataType, Node parent) {
			this.data = data;
			this.dataType = dataType;
			this.parent = parent;
		}

		public void insert(String data, FILETYPE dataType) {
			Node node = new Node(data, dataType, this);
			if (children == null) {
				children = new ArrayList<Node>();
			}
			children.add(node);
		}

		public String getData() {
			return data;
		}

		public FILETYPE getDataType() {
			return dataType;
		}

		public Node getParent() {
			return parent;
		}

		public List<Node> getChildren() {
			if (children == null) {
				children = new ArrayList<Node>();
			}
			return children;
		}

		public String getDirectory() {
			String path = data;
			Node temp = this;
			while (temp.getParent() != null){
				path = parent.getData() + "\\" + path;
				temp = temp.getParent();
			}
			return path;
		}
		
		public String toString() {
			return data;
		}
	}

}
