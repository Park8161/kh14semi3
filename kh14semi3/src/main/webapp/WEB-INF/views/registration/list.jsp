<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- header.jsp에 존재하는 내용을 불러오도록 설정 --%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<style>
	.table {
		border: 1px solid #2d3436;
		width: 100%;
		font-size: 16px;
		
	}
</style>

<script type="text/javascript">
	$(function(){
	
	
	});
</script>

<div class="container w-700 my-50">
	<div class="row center">
		<h1>강의 목록</h1>
	</div>

	<div class="row">
		<form action="list" method="get" autocomplete="off">
			<!-- 검색창 --> 
			<select class="field" name="column">
			<option value="lecture_department" <c:if test="${param.column == 'lecture_department'}">selected</c:if>>전공(학과)</option>
			<option value="lecture_professor" <c:if test="${param.column == 'lecture_professor'}">selected</c:if>>교수명</option>
			<option value="lecture_type" <c:if test="${param.column == 'lecture_type'}">selected</c:if>>분류</option>
			<option value="lecture_name" <c:if test="${param.column == 'lecture_name'}">selected</c:if>>강의명</option>
		</select>
		<input class="field" type="search" name="keyword" value="${param.keyword}">
			<button class="btn btn-neutral">검색</button>
		</form>
	</div>
	

	<div class="row">
		<c:choose>
			<%-- 결과가 없을 때 --%>
			<c:when test="${lectureList.isEmpty()}">
				<h2>결과가 존재하지 않습니다</h2>
			</c:when>
			<%-- 결과가 있을 때 --%>
			<c:otherwise>
				<!-- 결과 화면 -->
				<div class="right">
					<i class="fa-brands fa-slack red"></i> 강의명 클릭시 수강신청 담기 가능
				</div>
				<div class="flex-box">
				<table class="table table-horizontal w-20" style="border-right:none;">
					<thead>
						<tr>
							<th>전공(학과)</th>
						</tr>
					</thead>
					<tbody class="center">
						<c:forEach var="departmentDto" items="${departmentList}">
						<tr class="w-20">
							<td>${departmentDto.departmentName}</td>
						</tr>
						</c:forEach>
					</tbody>										
				</table>
				
				<table class="table table-horizontal w-80" style="border-left:none;">
					<thead>
						<tr>
							<!-- <th>전공(학과)</th> -->
							<th>교수명</th>
							<th>분류</th>
							<th width="30%">강의명</th>
							<th>강의시간</th>
							<th>강의실</th>
							<th>수강인원</th>
							<th>비고</th>
						</tr>
					</thead>
					<tbody class="center">					
						<c:forEach var="lectureDto" items="${lectureList}">
						<tr>
							<%-- <td>${lectureDto.lectureDepartment}</td> --%>
							<td>${lectureDto.lectureProfessor}</td>
							<td>${lectureDto.lectureType}</td>
							<td>
								<a href="/lecture/detail?lectureCode=${lectureDto.lectureCode}" class="link link-animation">
									${lectureDto.lectureName}
								</a>
							</td>
							<td>${lectureDto.lectureTime} ${lectureDto.lectureDuration} ${lectureDto.lectureDay}</td>
							<td>${lectureDto.lectureRoom}</td>
							<td>${lectureDto.lectureCount}</td>		
							<td></td>					
						</tr>					
						</c:forEach>
					</tbody>
				</table>
			</div>
				</c:otherwise>
			</c:choose>
	</div>

</div>


