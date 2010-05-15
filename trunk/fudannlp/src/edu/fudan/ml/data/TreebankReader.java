package edu.fudan.ml.data;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.nlp.parser.Tree;
public class TreebankReader {
	public static InstanceSet readTrees(String path, int from, int to,
			String suffix, Charset charset) throws IOException {
		List<File> fileList = findFiles(path, from, to, suffix);
		InstanceSet dataSet = new InstanceSet();
		for (File file : fileList) {
			TreeReaderIterator ite = new TreeReaderIterator(file, charset);
			while (ite.hasNext())	{
				dataSet.add(new Instance(ite.next()));
			}
		}
		return dataSet;
	}
	private static List<File> findFiles(String path, int from, int to,
			String suffix) {
		File fp = new File(path);
		List<File> fileList = new ArrayList<File>();
		appendFiles(fileList, fp, from, to, suffix);
		return fileList;
	}
	private static void appendFiles(List<File> fileList, File fp, int from,
			int to, String suffix) {
		if (fp.isDirectory()) {
			File[] nfiles = fp.listFiles();
			for (int l = 0; l < nfiles.length; l++)
				appendFiles(fileList, nfiles[l], from, to, suffix);
		} else if (fp.isFile()) {
			if (checkFileName(fp.getName(), from, to, suffix))
				fileList.add(fp);
		}
	}
	private static boolean checkFileName(String name, int from, int to,
			String suffix) {
		int fid = parseName(name);
		if (!name.endsWith('.' + suffix))
			return false;
		if (from == -1 && to == -1)
			return true;
		if (fid < from || fid > to)
			return false;
		return true;
	}
	public static InstanceSet readTrees(String path, String suffix,
			Charset charset) throws IOException {
		InstanceSet dataSet = new InstanceSet();
		List<File> fileList = findFiles(path, -1, -1, suffix);
		for (File file : fileList) {
			TreeReaderIterator ite = new TreeReaderIterator(file, charset);
			while (ite.hasNext())
				dataSet.add(new Instance(ite.next()));
		}
		return dataSet;
	}
	private static int parseName(String name) {
		int fid = 0;
		for (int i = 0; i < name.length(); i++) {
			if (Character.isDigit(name.charAt(i)))
				fid = fid * 10 + Character.digit(name.charAt(i), 10);
		}
		return fid;
	}
	private static class TreeReaderIterator implements Iterator<Tree<String>> {
		Tree<String> nextTree = null;
		PushbackReader in;
		public TreeReaderIterator(File file, Charset charset)
				throws IOException {
			this.in = new PushbackReader(new InputStreamReader(
					new FileInputStream(file), charset));
			nextTree = nextTree();
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
		public boolean hasNext() {
			return (nextTree != null);
		}
		public Tree<String> next() {
			Tree<String> tree = nextTree;
			nextTree = nextTree();
			return tree;
		}
		private Tree<String> nextTree() {
			Tree<String> tree = null;
			try {
				skipWhiteSpace();
				if (isLeftBracket())
					tree = readTree();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return tree;
		}
		private Tree<String> readTree() throws IOException {
			Tree<String> tree = null;
			if (isLeftBracket()) {
				in.read();
				String label = readLabel();
				tree = new Tree<String>(label);
				tree.setChildren(readChildren());
				in.read();
			} else {
				tree = new Tree<String>(readWord());
			}
			skipWhiteSpace();
			return tree;
		}
		private List<Tree<String>> readChildren() throws IOException {
			List<Tree<String>> children = new ArrayList<Tree<String>>();
			while (!isRightBracket()) {
				Tree<String> child = readTree();
				children.add(child);
				skipWhiteSpace();
			}
			return children;
		}
		private String readLabel() throws IOException {
			StringBuffer buf = new StringBuffer();
			int ch = in.read();
			if (ch != '(') {
				while (ch != ' ') {
					buf.append((char) ch);
					if (!isRightBracket())
						ch = in.read();
					else
						break;
				}
			} else {
				in.unread(ch);
			}
			skipWhiteSpace();
			if (buf.length() != 0)
				strip(buf);
			else
				buf.append("ROOT");
			return buf.toString().intern();
		}
		private void strip(StringBuffer buf) {
			int idx = buf.indexOf("=");
			int idx2 = buf.indexOf("-");
			if (idx2 > 0)	{
				if (idx == -1)
					idx = idx2;
				else
					idx = (idx < idx2 ? idx : idx2);
			}
			if (idx != -1)
				buf.delete(idx, buf.length());
		}
		private String readWord() throws IOException {
			StringBuffer buf = new StringBuffer();
			int ch = in.read();
			if (ch != '(') {
				while (ch != ' ') {
					buf.append((char) ch);
					if (!isRightBracket())
						ch = in.read();
					else
						break;
				}
			} else {
				in.unread(ch);
			}
			skipWhiteSpace();
			return buf.toString().intern();
		}
		private boolean isLeftBracket() throws IOException {
			boolean ret = false;
			int ch = in.read();
			in.unread(ch);
			if (ch == '(')
				ret = true;
			return ret;
		}
		private boolean isRightBracket() throws IOException {
			boolean ret = false;
			int ch = in.read();
			in.unread(ch);
			if (ch == ')')
				ret = true;
			return ret;
		}
		private void skipWhiteSpace() throws IOException {
			int ch;
			do {
				ch = in.read();
			} while (Character.isWhitespace(ch));
			in.unread(ch);
		}
	}
}
