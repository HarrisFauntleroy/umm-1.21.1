.PHONY: dev
dev:
	@echo "Starting Minecraft client and server..."
	gradlew runClient runServer

.PHONY: client
client:
	@echo "Starting Minecraft client..."
	gradlew runClient

.PHONY: server
server:
	@echo "Starting Minecraft server..."
	gradlew runServer

.PHONY: build
build:
	@echo "Building the mod..."
	gradlew build

.PHONY: clean
clean:
	@echo "Cleaning up..."
	gradlew clean
