package nano;

import nano.frame.FrameOption;
import nano.frame.SizeType;
import nano.frame.SortType;

public enum ModeType {
	MAIN("通常"),
	UNIT(FrameOption.UNIT,
			new SizeType[]{SizeType.UNIT1, SizeType.UNIT2},
			new SortType[]{SortType.ROW, SortType.COLUMN, SortType.TEAM}),
	SORT(FrameOption.SORT,
			new SizeType[]{SizeType.SORT1, SizeType.SORT2, SizeType.SORT3},
			new SortType[]{SortType.ROW, SortType.COLUMN});
	private String name;
	private FrameOption option;			// 
	private SizeType[] blockSize;		// 画像を配置するブロックの大きさ
	private SortType[] sortType;		// ソートアルゴリズム
	private ModeType(String name){this.name = name;}
	private ModeType(FrameOption option, SizeType[] blocksize, SortType[] sortType){
		name = option.toString();
		this.option = option;
		this.blockSize = blocksize;
		this.sortType = sortType;
	}
	public FrameOption getOption(){return option;};
	public SizeType[] getSizeList(){return blockSize;}
	public SortType[] getSortList(){return sortType;}
	@Override
	public String toString(){return name;}
}
