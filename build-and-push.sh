#!/bin/bash

# Kiểm tra xem người dùng đã nhập username Docker Hub chưa
if [ -z "$1" ]; then
    echo "Lỗi: Vui lòng cung cấp Username Docker Hub của bạn!"
    echo "Sử dụng: ./build-and-push.sh <your_dockerhub_username>"
    exit 1
fi

DOCKER_USER=$1
TAG="latest"

echo "========================================"
echo "BẮT ĐẦU BUILD & PUSH CINEHUB IMAGES"
echo "Username: $DOCKER_USER"
echo "Tag: $TAG"
echo "========================================"

# 1. Build & Push Backend
echo "--> 1. Đang build Backend..."
docker build -t $DOCKER_USER/cinehub-backend:$TAG ./CineHub
if [ $? -ne 0 ]; then 
    echo "Lỗi khi build Backend!"
    exit 1
fi

echo "--> Đang push Backend lên Docker Hub..."
docker push $DOCKER_USER/cinehub-backend:$TAG

# 2. Build & Push User Frontend
echo "--> 2. Đang build User Frontend..."
docker build -t $DOCKER_USER/cinehub-user:$TAG ./user
if [ $? -ne 0 ]; then 
    echo "Lỗi khi build User Frontend!"
    exit 1
fi

echo "--> Đang push User Frontend lên Docker Hub..."
docker push $DOCKER_USER/cinehub-user:$TAG

# 3. Build & Push Admin Frontend
echo "--> 3. Đang build Admin Frontend..."
docker build -t $DOCKER_USER/cinehub-admin:$TAG ./admin
if [ $? -ne 0 ]; then 
    echo "Lỗi khi build Admin Frontend!"
    exit 1
fi

echo "--> Đang push Admin Frontend lên Docker Hub..."
docker push $DOCKER_USER/cinehub-admin:$TAG

echo "========================================"
echo "HOÀN THÀNH! Đã push thành công cả 3 images lên Docker Hub."
echo "Hãy nhớ thêm dòng sau vào file .env của bạn:"
echo "DOCKER_USER=$DOCKER_USER"
echo "========================================"
