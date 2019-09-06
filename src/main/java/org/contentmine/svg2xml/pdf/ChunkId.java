package org.contentmine.svg2xml.pdf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.contentmine.graphics.AbstractCMElement;

public class ChunkId implements Comparable<ChunkId> {

	private static final String G = "g";

	public static Pattern ID_PATTERN = Pattern.compile("g\\.(\\d+)\\.(\\d+)");
	
	private int pageNumber;
	private int chunkNumber;
	private Integer subChunkNumber;

	private String id;
	
	public ChunkId(String id) {
		this.id = id;
		try {
			processId();
		} catch (Exception e) {
			throw new RuntimeException("cannot parse identifier: "+id, e);
		}
		throw new RuntimeException("CHECK NEVER USED");
	}
	
	public ChunkId(ChunkId chunkId) {
		this.pageNumber = chunkId.pageNumber;
		this.chunkNumber = chunkId.chunkNumber;
		this.subChunkNumber = chunkId.subChunkNumber;
		throw new RuntimeException("CHECK NEVER USED");
	}

	public ChunkId(int pageNumber, int ichunk) {
		this.pageNumber = pageNumber;
		this.chunkNumber = ichunk;
		this.subChunkNumber = null;
		throw new RuntimeException("CHECK NEVER USED");
	}

	private void processId() {
		Matcher matcher = ID_PATTERN.matcher(id);
		if (matcher.matches() && matcher.groupCount() == 2) {
			pageNumber = new Integer(matcher.group(1));
			chunkNumber = new Integer(matcher.group(2));
		} else {
			
		}
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getChunkNumber() {
		return chunkNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ChunkId) {
			ChunkId id = (ChunkId) o;
			return (this.toString().equals(id.toString()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 17 * pageNumber + 31 * chunkNumber + (subChunkNumber == null ? 0 : 127 * subChunkNumber);
	}
	
	public int compareTo(ChunkId chunk2) {
		int compare = 0;
		if (pageNumber < chunk2.pageNumber) {
			compare = -1;
		} else if (pageNumber > chunk2.pageNumber) {
			compare = 1;
		}
		if (compare == 0) {
			if (chunkNumber < chunk2.chunkNumber) {
				compare = -1;
			} else if (chunkNumber > chunk2.chunkNumber) {
				compare = 1;
			}
		}
		return compare;
	}

	public void setSubChunkNumber(int subChunkNumber) {
		this.subChunkNumber = subChunkNumber;
	}
	
	public String toString() {
		return createId();
	}

	private String createId() {
		return G+"."+pageNumber+"."+chunkNumber+(subChunkNumber == null ? "" : "."+subChunkNumber);
	}

	public static ChunkId createChunkId(AbstractCMElement gChunk) {
		return null;
	}
}
