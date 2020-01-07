# Server-less Email Micro-service
Java 8 source code, unit tests, and complimentary Javadoc for a materializing a light-weight email service.

Once built into an artifact, email delivery requests can be facilitated locally and remotely an AWS λƒ handler.

Upon receiving a valid request, the handler may look in an AWS S3 bucket to retrieve and load an HTML email template. 

Finally the email is sent using AWS SES and returns an email delivery response with status code and plain text body.


## Requisites
To successfully run tests and invoke mock requests, AWS credentials must be present in your home directory.  

In addition, invoking mock requests require both Docker and AWS SAM to be available on your machine.  

Working knowledge of aforementioned AWS components, containerization, and Lombok is recommended.

## Usage
```
# Step 1: Clone a single master branch of this repository and ignore superfluous repository history
git clone --single-branch --branch master https://github.com/nelsw/cinch-email-handler.git
  
# Step 2: Enter the directory of recently cloned branch 
cd cinch-email-handler

# Step 3: Execute the primary make command to clean and install the artififact.
make it 

# Step 4: Invoke a mock request use AWS sam local, see mock-request.json.
make invoke -e ROLE=<insert appropriate role here>

# Step 5: Create your own function.
make create -e ROLE=<insert appropriate role here>

# Step 5: Update the function.
make update -e ROLE=<insert appropriate role here>
```
_Note_ - The [Makefile](https://github.com/nelsw/hempconduit.com/blob/master/Makefile) contains all directives available to 
[make](https://en.wikipedia.org/wiki/Makefile) this software on `MacOS` and `LINUX` systems. Windows environments should
use `nmake` through Visual Studio or install `cygwin` from your favorite package manager.

### Further Information
- [Javadoc][javadoc] (autogenerated using [delomboked][delombok] [Lombok][lombok] and [UML Doclet][uml-doc]) 
- [AWS Simple Email Service (SES)][aws-ses]
- [AWS Lambda Function (λƒ)][aws-lambda]
- [AWS API Gateway (APIGW)][aws-apigw]
- [AWS Simple Storage Service (S3)][aws-s3]
- [AWS Elastic Load Balancer (ELB)][aws-elb]
- [AWS Serverless Application Model (SAM)][aws-sam] 
- [Docker: Enterprise Container Platform][docker]
- [Project Lombok][lombok]

[javadoc]: <https://htmlpreview.github.io/?https://raw.githubusercontent.com/nelsw/cinch-email-handler/master/docs/package-summary.html>
[uml-doc]: <https://github.com/talsma-ict/umldoclet>
[lombok]: <https://projectlombok.org/>
[delombok]: <https://projectlombok.org/features/delombok>
[aws-ses]: <https://aws.amazon.com/ses/>
[aws-elb]: <https://aws.amazon.com/elasticloadbalancing/>
[aws-lambda]: <https://aws.amazon.com/lambda/>
[aws-s3]: <https://aws.amazon.com/s3/>
[aws-apigw]: <https://aws.amazon.com/api-gateway/>
[aws-sam]: <https://aws.amazon.com/serverless/sam/>
[docker]: <https://www.docker.com/>