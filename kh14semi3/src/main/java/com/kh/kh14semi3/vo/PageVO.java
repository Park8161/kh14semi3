package com.kh.kh14semi3.vo;

import lombok.Data;

//페이징 작업을 모듈화 하기 위한 클래스
@Data
public class PageVO {
	//페이징 작업에 필요한 정보들을 필드로 선언
	private String column;//검색 항목
	private String keyword;//검색 키워드
	private int page=1;//페이지 번호
	private int size=10;//1페이지의 크기
	private int count;//총 데이터 개수
	private int blockSize = 10; //한 블럭구역의 크기
	
	//계산 메소드 (가상의 Getter 메소드) 추가
	public boolean isSearch() {
		return this.column != null && this.keyword != null;
	}

	
	//시작행, 종료행 계산 메소드
	public int getBeginRow() {
		return this.page * this.size - (this.size-1);
	}
	public int getEndRow() {
		return this.page * this.size;
	}
	
	//네비게이터를 위한 메소드
	public int getStartBlock() {
		return (this.page-1) /this.blockSize * this.blockSize +1;
	}
	 
	
	
	public boolean isFirst() {
		return this.getStartBlock() <= 1;
	}
	public boolean hasPrev() {
		return !this.isFirst();
	}
	public int getPrevBlock() {//이전 누르면 나올 페이지 번호
		return this.getStartBlock()-1;
	}
	public int getLastBlock() {
		return (this.count-1) / this.size+1;
	}
	public int getFinishBlock() {//표시할 마지막 블록번호
		int finishBlock = this.getStartBlock() + this.blockSize -1;
		return Math.min(finishBlock, this.getLastBlock());
	}
	public boolean isLast() {//마지막 구역인가요?
		return this.getFinishBlock() >= this.getLastBlock(); 
	}
	public boolean hasNext() {//다음 구역이 있어요?
		return this.isLast() == false;
	}
	public int getNextBlock() {//다음 구역 이동시 나올 번호
		return this.getFinishBlock()+1;
	}
}

