# Multi-Threaded Web Server Project

| **Name**  | **Student ID** | **Email**                  |
|:----------|:---------------|:---------------------------|
| Yu Shaoyu | 24118601G      | shaoyu.yu@connect.polyu.hk |


## Project Introduction

### Structure

```plaintext
src
  ├─main
  │  ├─java
  │  │  ├─bean          // bean classes
  │  │  ├─exception     // custom exceptions
  │  │  ├─test          // test cases
  │  │  ├─thread        // thread tools
  │  │  └─utils         // utility classes
  │  ├─resources        
  |  ├─Client.java      // command-line client (CLI)
  │  ├─HttpServer.java  // main server class
  |  └─HttpTask.java    // runable task for handling requests
resources               // resource file
  ├─helloworld.html     
  └─...    
```

### Dependencies

| **Name**      | **Version** | **Description**                       |
|:--------------|:------------|:--------------------------------------|
| commons-lang3 | 3.12.0      | Function parameter validation         |
| commons-cli   | 1.5         | Creating command-line interface (CLI) |

### How to Run

#### 0. compilation
> you can skip compilation by using `server.jar` directly
- use maven to compile the project
- enter `source_code` directory and run the following command:
```shell    
mvn clean package
```
- a jar file with dependencies (`server-jar-with-dependencies.jar`) will be generated under `target` folder
- copy the jar file to the root directory

#### 1. start the server
> at the root directory

`port` default to **8080**, `thread pool size` default to **3**

```shell
java -jar {path to jar} -p <port> -t <thread pool size>
```
use `--help` for help
```shell
java -jar {path to jar} --help
```

#### 2. use test client to mock requests

```shell
cat request.txt | xargs -0 java -cp server.jar test.TestClient <port>
```

## Features and Test Cases

### 1. GET Request

#### 200 OK
**request**
```http
GET /helloworld.html HTTP/1.1[CRLF]
Connection: close[CRLF]
User-Agent: Mozilla/5.0[CRLF]
Host: localhost:8080[CRLF]
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8[CRLF]
[CRLF]
```
**response**
```http
HTTP/1.1 200 OK[CRLF]
Server: Yu's Server[CRLF]
Connection: close[CRLF]
Last-Modified: Wed, 23 Oct 2024 21:53:26 CST[CRLF]
Content-Length: 205[CRLF]
Date: Thu, 24 Oct 2024 00:36:15 CST[CRLF]
Content-Type: text/html[CRLF]
[CRLF]
```

#### 304 Not Modified
**request**（`If-Modified-Since` date is set for 2025 ）
```http
GET /helloworld.html HTTP/1.1[CRLF]
Connection: close[CRLF]
User-Agent: Mozilla/5.0[CRLF]
If-Modified-Since: Wed, 23 Oct 2025 21:53:26 CST[CRLF]
Host: localhost:8080[CRLF]
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8[CRLF]
```

**response** 
```http
HTTP/1.1 304 Not Modified[CRLF]
Server: Yu's Server[CRLF]
Connection: close[CRLF]
Date: Thu, 24 Oct 2024 00:47:58 CST[CRLF]
[CRLF]
```

#### 404 Not Found
**request** （`helloworld.md` not exists under `resource` folder）
```http
GET /helloworld.md HTTP/1.1[CRLF]
Connection: close[CRLF]
User-Agent: Mozilla/5.0[CRLF]
Host: localhost:8080[CRLF]
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8[CRLF]
```

**response**
```http
HTTP/1.1 404 Not Found[CRLF]
Server: Yu's Server[CRLF]
Connection: close[CRLF]
Date: Thu, 24 Oct 2024 00:51:21 CST[CRLF]
[CRLF]
```

#### 400 Bad Request
**request**（malformed GET request: missing CRLF between headers and body）
```http
GET /helloworld.html HTTP/1.1[CRLF]
Host: localhost:8080[CRLF]
Connection: keep-alive[CRLF]
User-Agent: Mozilla/5.0[CRLF]
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
BODY[CRLF]
BODY[CRLF]
BODY
```

**response**
```http
HTTP/1.1 400 Bad Request[CRLF]
Server: Yu's Server[CRLF]
Connection: close[CRLF]
Date: Thu, 24 Oct 2024 01:02:50 CST[CRLF]
[CRLF]
```

### 2. HEAD Request
same as GET request, but without response body

### 3. Log
- Log files will be generated in the directory where the command is executed
- Log files are automatically generated and rotated daily (e.g. `server_2024-10-24.log`)
- For each request, there will be a log entry with the following format:
  - `timestamp` `log level` `[thread name]` `request` `response`
     - request format: `{host} {date} {resouce}`
     - response format: `{status} {reason phrase}`
  - example:
```log
2024-10-24 02:44:46 INFO [Thread-0] GET Request [localhost:8080 Thu, 24 Oct 2024 02:44:46 CST /helloworld.html] Response [404 Not Found]
2024-10-23 21:30:17 INFO [Thread-2] GET Request [localhost:8080 Wed, 23 Oct 2024 21:30:17 CST /helloworld.html] Response [200 OK]
```

###  4. Multi-threading
- Implement a thread pool with (fixed size) to allocate threads for handling requests
- The thread pool size can be set by the user when starting the server
- When thread pool is full, requests will be put into a queue and wait for available threads