# Java Blog Project Documentation: Portfolio Website with Dynamic Blog Functionality
Website: [here][1]
[1]: https://www.coursera.org/learn/introduction-to-algorithms](http://www.bytebounty.co.uk/blog/posts/
## 1. Introduction

This document outlines the development of a portfolio website featuring a dynamic blog section. The website is divided into distinct pages: Home, About, Qualifications, Experience, Skills, Portfolio, and Contact. While the Portfolio page is static, the Contact form utilizes Java for email functionality. The blog section, however, is a dynamic website, providing a platform for sharing content and engaging with visitors. 
This project utilizes a range of technologies for both frontend and backend development: 

### Frontend Technologies:
   
Thymeleaf orchestrates dynamic web page rendering, supported by Bootstrap for responsive design, while core web technologies (JavaScript, HTML, CSS) provide structure and interactivity. Enhancements in user experience come from jQuery for DOM manipulation, Summernote for WYSIWYG text editing, and Prism for syntax highlighting.

### Backend Technologies:
     
MySQL is used for reliable data storage, while Java Spring Boot accelerates web application development. Java JPA simplifies ORM, and Spring Security fortifies the application with robust security measures.
 
### Deployment: 
     
The project make use of AWS EC2 for cloud-based hosting and Docker for containerization and deployment, ensuring a seamless environment for the application.
 
This documentation will go through the design, implementation, and deployment details of the Java Blog Project, offering an overview of the project's architecture, technologies, and key features.  

## 2. Blog Functionality:
     
The blog functionality is essential, presenting users with the latest posts via a paginated grid accompanied by clear navigation options. Each post displays essential information, including thumbnails, titles, dates, categories, tags, summaries, and "Read More" buttons that lead to full-content views.

The interactive grid enables filtering by category or tag, enriching user engagement. Posts are identifiable through slugs, which ensure clean and readable URLs. Users can participate in discussions through comments, with registered users commenting post-login, while unregistered users can enter their name and email.

The comment management system offers capabilities to edit or delete personal comments, with real-time discussions facilitated through reply functionality.

### User Experience: 

The user experience framework involves secure login mechanisms, where users can create accounts, log in or out, and reset passwords. Features such as "Remember Me" and "Forgot Password" are implemented for convenience. User profiles allow customization via profile picture uploads and personal information editing.

An administrative control layer empowers administrators to manage user roles and privileges effectively, contributing to a secure environment.

The WYSIWYG editor allows seamless post creation and editing, with rich text formatting and image upload capabilities. Further, post management is structured around creation and editing timestamps, allowing drafts to be saved for later publication.

## 3. MVC Architecture and Security Considerations: 

The application architecture follows the Model-View-Controller (MVC) paradigm, ensuring clear separation of concerns. Controllers are responsible for handling requests and returning views, thereby encapsulating the application logic.

The project employs a robust security framework built using Spring Security. Authentication and authorization processes are streamlined through a custom login logic which directs users to a login page while offering options for both login and registration. The LoginSuccessHandler manages successful logins, tracking the last login time for user accounts. Role-based access control is enforced with a hierarchical delineation of permissions, ensuring that higher-level roles encompass the capabilities of lower-level roles efficiently.

The security configuration not only handles login processes but also implements CSRF protection, authorization rules for specific routes, and user session management. Notably, users are granted varying access to different functionalities based on their roles (Admin, Editor, Author, Subscriber), thereby enhancing application security.

In addition, sensitive information is secured through Docker secrets, with all credentials managed in a dedicated directory, safeguarding them from unauthorized access.

## 4. Third-Party Templates

To expedite development and maintain a professional aesthetic, two templates from HTMLCodex.com were employed: FreeFolio for the personal blog aspect, and Bloggy for enhanced blogging features. By leveraging these high-quality templates, the project achieves an appealing user interface while minimizing manual styling effort.

## 5. Challenges and Future Improvements

Throughout development, challenges arose, such as integrating the Summernote Editor effectively, particularly its capability for photo uploads without triggering deletions when necessary. Future objectives include refining this functionality to enhance user experience and creating additional features, such as subscribing to newsletters and automatic email notifications for comments.

Further improvements are also planned for the hierarchical roles implementation in Spring Boot. Enhancing the comments section and introducing modal functionalities will significantly improve user engagement. Additionally, transitioning to Docker Swarm for deployment could enhance scalability.
