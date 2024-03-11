# 개요
![image](https://github.com/uneugatoies-ttt/Blog/assets/149379411/321fcdba-4cc2-4edc-a73c-677450839de6)


### 어플리케이션의 소개:
이 어플리케이션은 간단한 블로그 사이트의 기능을 수행하기 위한 REST API들을 정의합니다.  
이것은 Spring Boot를 사용하는 백엔드 어플리케이션으로, 프론트엔드 어플리케이션에게 API를 제공하여 그로부터 들어오는 요청을 method와 URL에 따라 분류해 해당하는 API의 로직으로 처리합니다.

### 어플리케이션을 만든 목적:
제가 이 어플리케이션을 만든 목적은 Spring Boot를 사용한 프로젝트를 직접 제작해 보면서 그에 대한 이해도를 높이고 개발 과정에 익숙해지기 위함입니다.

### 개발 기간:
#### 23-12-10 ~ 24-01-21: 프로젝트 핵심 기능 구현

#### 24-01-22 ~ : 문서 작성과 코드 수정, 리팩토링
 




<br/>



# 프로젝트 회고
### 개발을 진행하며 배우게 된 것들:
- Spring application에서 빈번하게 사용되는 패턴들의 기본적인 특징과 그들을 이용할 때의 유용함에 대해서 알게 되었습니다.
- Java 클래스와 관계형 DB의 테이블 사이의 JPA를 통한 mapping의 기본적인 사항들과 사용 방법에 대해서 알게 되었습니다.  
- Spring application에서 자주 사용되는 인증 방식 중 token 기반 인증에 대한 개념과 JWT의 사용 방법에 대해서 알게 되었습니다.  
- Spring Security의 기본적인 동작과 설정 방법에 대해서 알게 되었습니다. 
- JUnit을 통한 unit test의 기본적인 방법과 그들이 가지는 의의에 대해서 알게 되었습니다.  
- Spring 기반의 backend app과 React/TypeScript 기반의 frontend app의 통합과 그러한 decoupled architecture를 사용하는 의미에 대해서 알게 되었습니다.  
- Spring application에서 validation (유효성 검사) 을 위해 사용할 수 있는 기능과 그에 따른 예외 처리에 대해서 알게 되었습니다.




### 아쉬운 점들 · 현 시점에서 수정과 리팩토링이 요구되는 것들 · 차후 프로젝트에서 보완하고 싶은 것들:
#### Layered architecture의 장점을 최대화하지 못한 점:
- 한 layer 내부의 변화가 어플리케이션의 다른 부분에 미치는 영향을 최소화할 수 있다는 것이 layered architecture의 장점이라고 알고 있습니다만, 그것을 과연 정말로 잘 살렸는지에 대해서는 회의적인 생각이 듭니다. 특히 presentation layer와 business layer 사이의 coupling의 조절에 대해서 관리를 제대로 하지 못했다고 판단합니다.
- 이 이유를 고찰해 보자면, controller-service 사이의 communication에 사용되는 method의 parameter와 return value에 사용될 수 있는 type을 묶어서 정리한 일종의 DTO를 정의해서 일괄적으로 그것만을 사용한 것이 아니라, 그때그때 임시방편 식으로 이 method에서는 DTO를, 또 다른 method에서는 String이나 기타 type들을 쓰는 형태로 다소 난잡하게 코드를 작성했기 때문이 아닌가 생각됩니다.
- 이에 대해서는, 특정 controller-service 사이의 parameter와 return value에 사용될 수 있는 type을 어떤 custom type 즉 DTO로 제한하고, 그것만을 사용하는 식으로 하면 좀 더 개선된 코드를 작성할 수 있지 않을까 판단합니다. 하지만 이것이 현장에서 일반적으로 받아들여지는 방법인지, 혹은 진정으로 decoupling에 도움을 주는 방식이라고 할 수 있는지에 대해서는 아직 완전하게 알 수 없는 상태입니다.

#### test code 작성 당시 unit test의 목적을 완전히 이해하지 못했던 점:
- 예를 들어 test code를 작성하는 지침이 되는 FIRST 원리에 따르면 보통 test는 test의 대상이 되는 code가 완성되기 이전에 수행되어야 하지만, 이 project는 거의 모든 기능의 구현이 완료되어 갈 즈음에 test code가 작성되기 시작했습니다. 물론 제 자신의 성장의 관점에서 보면 이를 작성하는 과정을 통해서 unit test 작성의 일반적인 패턴에 더욱 익숙해지는 것이 가능했지만, 만일 이것을 실제 개발 환경이라고 가정한다면 저는 거의 의미가 없는 행위를 한 것입니다.
- 또한, 보통 가능한 한 다양한 시나리오를 포괄적으로 작성하는 것이 test code의 작성에 있어서는 바람직하다고 여겨지지만, 이 project는 그 test code에서 지나치게 한정적인 시나리오만을 상정하고 작성이 진행되었습니다.

#### REST API의 특성을 좀 더 심도있게 고려하지 못한 점:
- OAuth의 흐름이 진행될 때 코드에 session을 사용하는 부분이 있습니다. Frontend application에서 signin을 마친 사용자가 username을 사용하게 되는 경우가 있으므로 이것을 이후 redirect된 URL의 parameter로 지정해주기 위함입니다. 하지만, 일반적으로 REST API에서는 statelessness (상태 없음) 의 제한 사항을 지키기 위해 session을 사용하지 않는다고 알고 있습니다. 처음에는 무심코 사용했다가 이후 다시금 REST API에 대해 복습하다가 이에 대해 깨닫게 되어 돌아와서 대안을 생각해 봤지만, 수단이 딱히 생각나지 않았기 때문에 여전히 고쳐지지 않은 상태입니다.


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
![APIUser](https://github.com/uneugatoies-ttt/Blog/assets/149379411/e00e90da-91f3-410a-b9de-87be51703779)


<br/>

### 블로그 게시물 관련:
![APIArticle](https://github.com/uneugatoies-ttt/Blog/assets/149379411/e766f5ea-27f5-4029-baca-72ac5478b76c)

![APIBlog](https://github.com/uneugatoies-ttt/Blog/assets/149379411/f9db763b-3689-4388-8350-53b8b0c3e1ff)


<br/>

### 카테고리 관련:
![APICategory](https://github.com/uneugatoies-ttt/Blog/assets/149379411/7dc0dd7c-1c45-45a8-ab85-7f9cead7d878)


<br/>

### 태그 관련:
![APITag](https://github.com/uneugatoies-ttt/Blog/assets/149379411/beced387-03d5-4869-936d-15f965a87ee9)


<br/>

### 댓글 관련:
![APIReply](https://github.com/uneugatoies-ttt/Blog/assets/149379411/b24af9f3-856f-41a0-8179-dcb5e00b9e9c)


<br/>

### 알림 메시지 관련:
![APINotificationMessage](https://github.com/uneugatoies-ttt/Blog/assets/149379411/4aaa97c2-62b9-44e9-a71c-ca10a50a4619)


<br/>

### 파일 관련:
![APIFile](https://github.com/uneugatoies-ttt/Blog/assets/149379411/a3b41b7e-0a7d-45cd-b19e-88b56de1c380)




<br/>



# DB 구조
![image](https://github.com/uneugatoies-ttt/Blog/assets/149379411/a658ee25-2dea-40fa-87f6-989089e8b367)

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



# 사용 예시
<details>
<summary><h3>회원과 인증:</h3></summary>
<h4>회원 가입과 로그인:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/e394e4a1-4882-41b2-b785-b13fefc33549" alt="login">

<li>사용자의 이름(user ID)와 비밀번호를 사용해 로그인합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/5c4cd5fe-6df2-41a3-92ae-c1ce4ce8bd23" alt="signup">

<li>사용자의 이름, 비밀번호, 그리고 이메일을 입력해 회원 가입합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/a78a311b-197b-4470-bcc1-727518924a55" alt="newblog">

<li>회원 가입을 마치면 자동적으로 블로그가 생성됩니다.</li>
</ul>

<br/>

<h4>OAuth2를 사용한 회원 가입과 로그인:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/020d7ea1-1fdc-47ab-be6d-eebd5ea02457" alt="oauth2google">

<li>Google 계정을 사용해서 이 어플리케이션에서 인증합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/a285cb7c-d13a-4db7-b242-197e947d2a5a" alt="oauth2github">

<li>GitHub 계정을 사용해서 이 어플리케이션에서 인증합니다.</li>
</ul>

<br/>
<h4>회원 탈퇴:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/e6e61420-d355-422c-a3ff-58c87fdab397" alt="deleteaccount">

<li>사용자의 이름과 비밀번호를 입력해 탈퇴합니다.</li>
</ul>
<br/>
</details>


<details>
<summary><h3>블로그 게시물:</h3></summary>
<h4>게시물 열람:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/06538b77-25e5-4a14-92f3-fe9f8fcb16df" alt="browsing1">
<li>블로그에 속하는 게시물들을 열람합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/069345b0-a97b-4907-8a62-21677ead9042" alt="browsing2">
<li>게시물의 내용을 열람합니다.</li>
</ul>
<br/>

<h4>게시물 작성:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/7866eb1b-a1b3-493b-b5ee-568e6d528dab" alt="creating1">

<li>제목, 내용, 대표 이미지, 카테고리, 그리고 태그를 입력하거나 지정해 게시물을 작성합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/6391f583-aa99-4b18-9cdf-34d9bbe3aada" alt="creating2">

<li>게시물이 작성된 이후의 블로그의 메인 화면을 열람합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/9207c1ea-6c80-4e5e-9180-4757cbadcc95" alt="creating3">

<li>새로 작성된 게시물의 내용을 열람합니다.</li>
</ul>
<br/>

<h4>게시물 수정:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/f49c2058-739a-4154-acc2-ac1d739b8498" alt="modifying1">

<li>제목, 내용, 대표 이미지, 카테고리, 그리고 태그를 변경해서 게시물을 수정합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/6b70459f-274d-44dc-92de-fce826f712e1" alt="modifying2">

<li>수정된 게시물의 내용을 열람합니다.</li>
</ul>
<br/>

<h4>게시물 삭제:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/101d1a44-2f26-4b1d-9db2-69ee375c65fe" alt="deleting1">
![deleting1]()

<li>게시물의 내용에 표시된 버튼을 눌러 게시물을 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/07b5c3c9-27c6-4e26-8abf-9bdad3ac209f" alt="deleting2">

<li>게시물이 삭제된 이후의 블로그의 메인 화면을 열람합니다.</li>
</ul>
<br/>
</details>


<details>
<summary><h3>카테고리와 태그:</h3></summary>
<h4>카테고리와 태그 추가와 명칭 수정:</h4>
<ul>
 <img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/afce8409-23e0-4179-8571-9c0868ff7c59" alt="ct1">

<li>카테고리를 추가, 수정, 혹은 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/8aa24302-da0b-4ecd-836c-55e23904021a" alt="ct2">

<li>태그를 추가, 수정, 혹은 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/8af5f71e-709c-46c2-8e7a-5e3edbc99c90" alt="ct3">

<li>추가, 수정, 혹은 삭제를 마친 이후의 카테고리와 태그의 목록을 확인합니다.</li>
</ul>
<br/>

</details>


<details>
<summary><h3>댓글:</h3></summary>
<h4>댓글 추가:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/6d6bd10f-194d-4b3e-a795-2744aed561f6" alt="adding1">

<li>게시물의 하단에서 댓글을 작성합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/b1979d6c-2a05-4ea1-b0dc-f07398f28fa1" alt="adding2">

<li>추가된 댓글을 확인합니다.</li>
</ul>
<br/>

<h4>댓글 수정:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/24dedb7a-aee9-4e82-8a05-5e2f7c1ead39" alt="editing1">

<li>기존의 댓글을 수정합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/1eb38ba6-cceb-4235-86e8-c8ec5b75746b" alt="editing2">

<li>수정된 댓글을 확인합니다.</li>
</ul>
<br/>

<h4>댓글 삭제:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/d11982c6-8884-4a8d-9d84-62552c2242d9" alt="deleting1">

<li>기존의 댓글을 삭제합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/20a7fdbc-1bf0-43e4-adda-72ac1db2839d" alt="deleting2">

<li>삭제된 댓글을 확인합니다.</li>
</ul>
<br/>

</details>


<details>
<summary><h3>알림 메시지:</h3></summary>
<h4>알림 메시지 확인:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/8b9ed1a1-cabe-4ebe-97be-f9393b593580" alt="confirming1">

<li>새로운 댓글을 작성합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/89da5125-cf4a-4efb-96d5-3d13a63f712c" alt="confirming2">

<li>댓글이 달린 게시물의 작성자에게 알림 메시지가 전달된 것을 확인합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/c8ddc80d-b5c0-4596-a0b1-e870bfcd9ead" alt="confirming3">

<li>드롭다운 메뉴를 클릭해 알림 메시지의 목록을 확인합니다.</li>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/4eb55c4c-b38c-4c3a-83ea-e04401f1626c" alt="confirming4">

<li>드롭다운 메뉴의 메시지를 클릭해 해당 댓글이 달린 페이지로 이동합니다.</li>
</ul>
<br/>

<h4>알림 메시지 삭제:</h4>
<ul>
<img src="https://github.com/uneugatoies-ttt/Blog/assets/149379411/809b4cab-f8f6-4e9a-a565-43c57ad90928" alt="deleting1">

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

