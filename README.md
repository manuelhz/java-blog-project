# Java Blog Project Documentation: Portfolio Website with Dynamic Blog Functionality

## 1. Introduction

This document outlines the development of a portfolio website featuring a dynamic blog section. The website is divided into distinct pages: Home, About, Qualifications, Experience, Skills, Portfolio, and Contact. While the Portfolio page is static, the Contact form utilizes Java for email functionality. The blog section, however, is a dynamic website, providing a platform for sharing content and engaging with visitors. 
This project leverages a range of technologies for both frontend and backend development: 

### Frontend:
   
* **Thymeleaf:**  A powerful template engine for creating dynamic web pages.     
* **Bootstrap:**  A popular framework for responsive web design.     
* **JavaScript, HTML, CSS:** Core web technologies for structuring, styling, and interactivity.     
* **jQuery:** A JavaScript library for simplifying DOM manipulation and event handling.     
* **Summernote:** A WYSIWYG editor for rich text content creation.     
* **Prism:**  A library for code highlighting, improving the readability of code snippets.

### Backend:
     
* **MySQL:**  A relational database for storing data.     
* **Java Spring Boot:**  A framework for building web applications quickly and efficiently.     
* **Java JPA:** A specification for object-relational mapping (ORM).     
* **Spring Security:**  A framework for securing web applications.
 
### Deployment: 
     
* **AWS CS2:**  Cloud infrastructure for hosting the application.     
* **Docker:**  A containerization technology for packaging and deploying the application.
 
This documentation will delve into the design, implementation, and deployment details of the Java Blog Project, offering a comprehensive overview of the project's architecture, technologies, and key features. 

## 2. Blog Functionality:
     
* **Dynamic Blog Pages:** The blog displays the latest posts in a paginated grid, with clear navigation options.
* **Post Details:** Each post showcases a thumbnail image, title, date, categories, tags, a summary, and a "Read More" button for accessing the full content.
* **Interactive Grid:** The post grid allows users to filter posts by category or tag, providing a seamless browsing experience.
* **User-Friendly Navigation:** The user can easily navigate between posts using the "Next" and "Previous" buttons or by directly accessing posts from the grid.
* **Post Slugs:**  Posts, categories, and tags are uniquely identified by their slugs, ensuring clean and readable URLs.
* **Comment Section:** Users can engage in discussions by leaving comments on posts. Registered users can comment after login, while unregistered users can fill in their name and email.
* **Comment Management:**  Users can edit or delete their own comments. Additionally, administrators and moderators have the authority to delete comments.
* **Reply Functionality:**  Users can engage in threaded conversations by replying to existing comments.

### User Experience: 

* **Secure Login:** Users can create accounts, sign in, sign out, reset passwords, and benefit from features like "Remember Me" and "Forgot Password".

* **Personalized Profiles:**  Users can upload profile pictures, edit their information, and manage their accounts.

* **Administrative Control:**  Administrators can manage user accounts, roles, and privileges, ensuring a secure and well-organized platform.
     
* **WYSIWYG Editor:**  Writers can easily create and edit blog posts using a user-friendly WYSIWYG editor, eliminating the need for HTML coding.
     
* **Rich Text Formatting:**  The editor supports various formatting options like font size, bold, italics, alignment, and more.
     
* **Image Upload:** Writers can upload images directly into their blog posts, enhancing visual appeal.
     
* **Post Management:**  Blog posts are created with a "created" and "edited" date, allowing users to track changes. Posts can be saved as drafts for later editing and publication.

### Role-Based Access Control: 

* **Hierarchical Roles:**  The system implements a clear hierarchy of roles with distinct privileges, preventing overlapping permissions and ensuring security.
     
* **Defined Roles and Privileges:**  Roles like Administrator, Editor, Author, and Subscriber are established with specific permissions, granting access to different functionalities based on user roles.

## 3. Deployment

Hereby the steps followed to deploy the Java Spring Boot Blog application on an AWS EC2 instance utilizing Docker and Docker Compose. The deployment process involves building and pushing the Docker image, setting up the EC2 instance, and managing the database.

The first step in deploying the application is to build the Docker image for the web server and push it to Docker Hub. The following steps outline this process:

### Clean and Package the Application:

```
mvn clean package
```


### Using Docker Compose, build the image by executing:
```
docker-compose up --build
```    

### Log in to Docker Hub using the command:
```
docker login
``` 

### Tag and Push the Docker image that was built:
```
docker tag image-name dockerhub-repo/image-name
docker push dockerhub-repo/image-name
``` 

## Setup AWS EC2 Instance

### Create an EC2 instance running Ubuntu 14.04 through the AWS Management Console.
### SSH into the EC2 Instance: Connect to the EC2 instance using SSH:
```
ssh -i "amazon.pem" ubuntu@<instance_public_dns_or_ip>
``` 
### After successfully connect to the EC2 instance, install Docker:
```
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
``` 

### Enable and start the Docker service with:
```
sudo systemctl enable docker
sudo systemctl start docker
``` 

### Verify that the Docker Engine installation is successful by running the hello-world image.
```
sudo service docker start
sudo docker run hello-world
``` 

### Manage Docker as a non-root user creating the docker group and adding your user to the docker group:
```
sudo groupadd docker
sudo usermod -aG docker $USER
``` 

### Log in to Docker Hub on the EC2 instance using:
```
sudo docker login
``` 


## Upload Neccesary Files to the Server.

### Use rsync and scp to transfer the docker-compose.yml, SQL dumps, environment variables, configuration files, etc to the EC2 instance:
```
rsync -e "ssh -i amazon.pem" -avz ~/docker-compose.yml ubuntu@<instance_public_dns_or_ip>:~/myfolder/
scp -i amazon.pem ~/backup.sql ubuntu@<instance_public_dns_or_ip>:~/
``` 


## Create Docker Volumes

### Before running the application I created the necessary Docker volumes that are external to the docker-compose file:

```
docker volume create db-vol
docker volume create static-vol
docker volume create templates-vol
``` 

## Run Docker Compose

### Pull the Web Server Image:
```
docker pull dockerhub-repo/image-name
``` 

### Start Services: After navigate to the directory containing docker-compose.yml run:
```
docker-compose up
``` 

## Restore the Database


### To restore the database from the backup SQL file, I executed the following command:

```
mysql -u <db_user> -p <db_name> < ~/backup.sql
``` 

Following the precedent steps I successfully deployed the Java Spring Boot Blog application on AWS EC2 instance using Docker.