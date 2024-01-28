# 개요
### 어플리케이션의 소개:
이 어플리케이션은 간단한 블로그 사이트의 기능을 수행하기 위한 REST API들을 정의합니다.  
이것은 Spring Boot를 사용하는 백엔드 어플리케이션으로, 프론트엔드 어플리케이션에게 API를 제공하여 그로부터 들어오는 요청을 method와 URL에 따라 분류해 해당하는 API의 로직으로 처리합니다.

### 어플리케이션을 만든 목적:
제가 이 어플리케이션을 만든 목적은 Spring Boot를 사용한 프로젝트를 직접 제작해 보면서 그에 대한 이해도를 높이고 개발 과정에 익숙해지기 위함입니다.

### 개발 기간:
#### 23-12-10 ~ 24-01-21: 프로젝트 핵심 기능 구현

#### 24-01-22 ~ : 문서 작성과 리팩토링
 




<br/>



# 프로젝트 회고
### 개발을 진행하며 배우게 된 것들:
- Spring application에서 빈번하게 사용되는 패턴들의 기본적인 특징과 그들을 이용할 때의 유용함에 대해서 알게 되었습니다. <br/>
- Java 클래스와 관계형 DB의 테이블과의 JPA를 통한 mapping의 기본적인 사항들과 사용 방법에 대해서 알게 되었습니다.  <br/>
- Spring application에서 자주 사용되는 인증 방식 중 token 기반 인증에 대한 개념과 JWT의 사용 방법에 대해서 알게 되었습니다.  <br/>
- Spring Security의 기본적인 동작과 설정 방법에 대해서 알게 되었습니다.  <br/>
- JUnit을 통한 unit test의 기본적인 방법과 그들이 가지는 의의에 대해서 알게 되었습니다.  <br/>
- Spring 기반의 backend app과 React/TypeScript 기반의 frontend app의 통합과 그러한 decoupled architecture를 사용하는 의미에 대해서 알게 되었습니다.  <br/>
- Spring application에서 validation (유효성 검사) 을 위해 사용할 수 있는 기능과 그에 따른 예외 처리에 대해서 알게 되었습니다.  <br/>



### 아쉬운 점들 · 차후 프로젝트에서 보완하고 싶은 것들:
#### Layered architecture의 장점을 최대화하지 못한 점:
-

#### test code 작성 당시 unit test의 목적을 완전히 이해하지 못했던 점:
-

#### 


<br/>




# 기술 스택
### 언어:
- Java 11
### 프레임워크:
- Spring Boot 2.7.15
### 빌드 도구:
- Gradle 8.5
### ORM:
- JPA
- Spring Data JPA
### 데이터베이스:
- MariaDB
### 보안:
- Spring Security
- JWT
- OAuth2
### 테스트:
- JUnit 5



<br/>



# 주요 기능
### 회원과 인증 관련 기능:
#### 일반 회원 가입, 로그인 및 회원 탈퇴:
- 사용자는 닉네임, 비밀번호, 이메일을 입력해 회원 가입을 할 수 있습니다.
- 등록된 사용자에게는 자동으로 블로그가 하나 할당됩니다.
- 등록된 사용자는 닉네임과 비밀번호를 입력해 로그인을 할 수 있습니다.
- 등록된 사용자는 닉네임과 비밀번호를 입력해 회원 탈퇴를 할 수 있습니다.
#### OAuth2 회원 가입 및 로그인:
- Google과 GitHub의 계정을 이용해서 이 어플리케이션에서의 인증을 할 수 있습니다.

<br/>

### 블로그 게시물 관련 기능:
#### 게시물 작성, 수정 및 삭제:
- 등록된 사용자는 자신의 블로그에서 게시물 작성, 수정 및 삭제를 할 수 있습니다.
#### 게시물 열람:
- 등록된 혹은 등록되지 않은 사용자는, 자신 혹은 타인의 블로그에서 해당하는 게시물을 열람할 수 있습니다.

<br/>

### 카테고리 관련 기능:
#### 카테고리 추가, 명칭 수정 및 삭제:
- 등록된 사용자는 자신의 블로그에서 카테고리 추가, 명칭 수정 및 삭제를 할 수 있습니다.
#### 카테고리에 따른 게시물 열람:
- 등록된 혹은 등록되지 않은 사용자는, 자신 혹은 타인의 블로그에서 카테고리에 따라 분류된 게시물을 열람할 수 있습니다.

<br/>

### 태그 관련 기능:
#### 태그 추가, 명칭 수정 및 삭제:
- 등록된 사용자는 자신의 블로그에서 태그 추가, 명칭 수정 및 삭제를 할 수 있습니다.
#### 태그에 따른 게시물 열람:
- 등록된 혹은 등록되지 않은 사용자는, 자신 혹은 타인의 블로그에서 태그에 따라 분류된 게시물을 열람할 수 있습니다.

<br/>

### 댓글 관련 기능:
#### 댓글 작성, 수정 및 삭제:
- 등록된 사용자는 자신 혹은 타인의 블로그에서 댓글 작성, 수정 및 삭제를 할 수 있습니다.

<br/>

### 알림 메시지 관련 기능:
#### 알림 메시지 확인 및 삭제:
- 등록된 사용자는 자신의 블로그의 게시물에 댓글이 달린 것을 메인 화면에서의 메시지로 확인할 수 있습니다.
- 등록된 사용자는 드롭다운 메뉴의 버튼을 클릭해 자신에게 해당하는 메시지들을 일괄적으로 삭제할 수 있습니다.



<br/>



# API 구조
### 회원과 인증 관련:
![APIUser](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/c4eca181-905c-4f56-8460-3f200f573d54)

<br/>

### 블로그 게시물 관련:
![APIArticle](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/cab4f590-5565-4898-82d9-7314caf73fbb)
![APIBlog](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/4569504c-e06d-402e-a663-c48f580529c6)

<br/>

### 카테고리 관련:
![APICategory](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/c25b48e3-1eea-4462-8860-824e7d8bdb9c)

<br/>

### 태그 관련:
![APITag](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/90b488fd-b8e2-4c01-8e15-921fc9bdb930)

<br/>

### 댓글 관련:
![APIReply](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/9f21db8f-f7d0-4d6e-8f68-02e099acc476)

<br/>

### 알림 메시지 관련:
![APINotificationMessage](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/8952995e-56a6-42df-8e2a-455868a4cd0d)

<br/>

### 파일 관련:
![APIFile](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/3b8f2cdf-b976-451d-aa5d-6ae17aedc8f3)



<br/>



# DB 구조
![DB 구조](https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/0f8e3513-0161-4aab-af45-d40c367bf264)
### 테이블:
#### article:
- 이 어플리케이션의 게시물을 나타냅니다.
#### user:
- 이 어플리케이션의 등록된 사용자를 나타냅니다.
#### category:
- 게시물들이 분류되는 카테고리를 나타냅니다.
- 하나의 게시물은 오직 하나의 카테고리만을 가질 수 있습니다.
#### tag:
- 게시물들이 분류되는 태그를 나타냅니다.
- 하나의 게시물은 여러 개의 태그를 가질 수 있습니다.
#### article_tag:
- article과 tag 사이의 다대다 관계를 나타냅니다.
#### file:
- 게시물의 대표 이미지 파일을 나타냅니다.
#### notification_message:
- 사용자들에게 전달되는 알림 메시지를 나타냅니다.
#### reply:
- 게시물들에 붙은 댓글을 나타냅니다.



<br/>





# 기능 상세
### 인증과 보안:

### 파일 처리:

### FE-BE 상호 작용:



<br/>



# 사용 예시
<details>
<summary><h3>회원과 인증:</h3></summary>
<h4>회원 가입과 로그인:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/f99a2210-e8f6-4704-828d-546837998260" alt="login">
<li>사용자의 이름(user ID)와 비밀번호를 사용해 로그인합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/fe3f04bc-b7d6-4125-96a3-160351809746" alt="signup">
<li>사용자의 이름, 비밀번호, 그리고 이메일을 입력해 회원 가입합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/e9395bc9-2f9e-4101-a0f1-1bdd50ac8623" alt="newblog">
<li>회원 가입을 마치면 자동적으로 블로그가 생성됩니다.</li>
</ul>

<br/>

<h4>OAuth2를 사용한 회원 가입과 로그인:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/fd5254aa-5d0d-4a9d-b371-e292c68d9ad9" alt="oauth2google">
<li>Google 계정을 사용해서 이 어플리케이션에서 인증합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/552db43f-10da-44f4-a79a-25b730961e9b" alt="oauth2github">
<li>GitHub 계정을 사용해서 이 어플리케이션에서 인증합니다.</li>
</ul>

<br/>
<h4>회원 탈퇴:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/22cd7355-2249-4716-888e-225747a225bb" alt="deleteaccount">
<li>사용자의 이름과 비밀번호를 입력해 탈퇴합니다.</li>
</ul>
<br/>
</details>


<details>
<summary><h3>블로그 게시물:</h3></summary>
<h4>게시물 열람:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/31ff5106-6b24-4b6d-baaa-c5a8a479d23d">
<li>블로그에 속하는 게시물들을 열람합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/62c61738-5468-46c2-95f9-dd6732cdcd31">
<li>게시물의 내용을 열람합니다.</li>
</ul>
<br/>

<h4>게시물 작성:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/a34a2e5e-e5aa-49d3-964b-caeecf7aaafe">
<li>제목, 내용, 대표 이미지, 카테고리, 그리고 태그를 입력하거나 지정해 게시물을 작성합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/cb3f757c-9711-46a0-b11c-0c031c1ba259">
<li>게시물이 작성된 이후의 블로그의 메인 화면을 열람합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/05ed7fdf-94bf-42a8-b91a-74433893f9d5">
<li>새로 작성된 게시물의 내용을 열람합니다.</li>
</ul>
<br/>

<h4>게시물 수정:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/e9f72ba7-a156-4b7b-9696-399b909d1fe2">
<li>제목, 내용, 대표 이미지, 카테고리, 그리고 태그를 변경해서 게시물을 수정합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/f31a3ffb-03b8-497c-b05f-30bd00b4740d">
<li>수정된 게시물의 내용을 열람합니다.</li>
</ul>
<br/>

<h4>게시물 삭제:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/72bc2129-dd2d-4682-8859-83871414df18">
<li>게시물의 내용에 표시된 버튼을 눌러 게시물을 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/54d53836-6a5a-4c1a-83fe-63dd508dbdbb">
<li>게시물이 삭제된 이후의 블로그의 메인 화면을 열람합니다.</li>
</ul>
<br/>
</details>


<details>
<summary><h3>카테고리와 태그:</h3></summary>
<h4>카테고리와 태그 추가와 명칭 수정:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/cb38579a-a4a9-4e3a-a487-a8626e1bf477">
<li>카테고리를 추가, 수정, 혹은 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/e6769172-7b44-4f9b-aac4-ba09bcb2c034">
<li>태그를 추가, 수정, 혹은 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/b798c68f-8041-414a-8377-3dc0ec122aa4">
<li>추가, 수정, 혹은 삭제를 마친 이후의 카테고리와 태그의 목록을 확인합니다.</li>
</ul>
<br/>

</details>


<details>
<summary><h3>댓글:</h3></summary>
<h4>댓글 추가:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/f118f9eb-1aab-414c-bc19-7bc9169ca73b">
<li>게시물의 하단에서 댓글을 작성합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/7f82d83b-8f44-4806-b435-f82534ee1189">
<li>추가된 댓글을 확인합니다.</li>
</ul>
<br/>

<h4>댓글 수정:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/417b6297-1e3c-4b8f-9771-b833374a19c7">
<li>기존의 댓글을 수정합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/3b456966-8273-46b7-813c-562c6801e9f5">
<li>수정된 댓글을 확인합니다.</li>
</ul>
<br/>

<h4>댓글 삭제:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/0bc2a35c-3a15-4019-b2cd-f4624e88bc5f">
<li>기존의 댓글을 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/d4050468-e997-40d8-9fca-63bf46a93378">
<li>삭제된 댓글을 확인합니다.</li>
</ul>
<br/>

</details>


<details>
<summary><h3>알림 메시지:</h3></summary>
<h4>알림 메시지 확인:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/8b78e3c9-bbc8-4f9c-8e18-6d320e4ad0a3">
<li>새로운 댓글을 작성합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/f79a9b78-dcb5-4d1b-820f-26829c0fe4cd">
<li>댓글이 달린 게시물의 작성자에게 알림 메시지가 전달된 것을 확인합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/55f81c11-dff3-43ef-8450-a74fbd8b54a3">
<li>드롭다운 메뉴를 클릭해 알림 메시지의 목록을 확인합니다.</li>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/9b253338-35b5-4762-9fb7-5b23757c117c">
<li>드롭다운 메뉴의 메시지를 클릭해 해당 댓글이 달린 페이지로 이동합니다.</li>
</ul>
<br/>

<h4>알림 메시지 삭제:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/BlogFE/assets/149379411/db3c3ce3-874a-4400-afa4-32837879b4b7">
<li>드롭다운 메뉴의 버튼을 클릭해 모든 메시지를 삭제합니다.</li>
</ul>
<br/>

</details>



<br/>




# 프론트엔드
### 프론트엔드 개요:
이 어플리케이션은 본 프로젝트의 백엔드 어플리케이션과 HTTP 요청과 응답을 통해 상호작용합니다.  
이것은 TypeScript와 React를 사용하는 프론트엔드 어플리케이션으로, 브라우저 상에서 페이지를 표시하여 사용자들로부터 입력을 받고 그에 따른 처리를 하며, 이 과정에서 요구되는 리소스 등의 필요 사항을 백엔드에게 전달하여 그로부터 받은 응답을 사용해서 자신의 기능을 수행합니다.

### 프론트엔드 기술 스택:
#### 언어:
- TypeScript
#### 라이브러리:
- React




</br>


<br/>

