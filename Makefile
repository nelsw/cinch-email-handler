# envrionment variables in the scope of this file, overwide with flag -e
JAR = handler-1.0.0.jar
NAME = emailHandler
ROLE =

# A phony target is one that is not really the name of a file, but rather a sequence of commands.
# We use this practice to avoid potential naming conflicts with files in the home environment but
# also improve performance by telling the SHELL that we do not expect the command to create a file.
.PHONY: it clean install test invoke update create

it: clean install

clean:
	mvn clean

install:
	mvn install

invoke: install
	sam local invoke -e mock-request.json ${NAME}

update: install
	aws lambda update-function-code --function-name ${NAME} --zip-file fileb://./${JAR};\
	aws lambda update-function-configuration --function-name ${NAME} ;\

create: install
	aws lambda create-function \
	--function-name ${NAME} \
	--role ${ROLE} \
	--zip-file fileb://./${JAR} \
	--handler Handler::handlerRequest \
	--runtime java8 \
	--memory-size 512 \
	--timeout 30;