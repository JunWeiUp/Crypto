# 加密货币钱包应用

## 项目概述

这是一个基于 Android 的加密货币钱包应用，用于展示用户的加密货币资产。应用采用现代化的架构设计，支持多种加密货币的余额和汇率展示，并提供了良好的用户体验和错误处理机制。

## 下载

你可以从 GitHub Releases 页面下载最新版本的 APK：

[![GitHub release (latest by date)](https://img.shields.io/github/v/release/用户名/仓库名?label=下载)](https://github.com/用户名/仓库名/releases/latest)

## 功能特点

- **多币种支持**：支持 BTC、ETH、CRO、USDT、DAI 等多种加密货币
- **实时汇率**：显示各种加密货币对 USD 的实时汇率
- **资产总览**：展示所有加密货币的总价值（USD）
- **详细信息**：每种货币显示名称、图标、余额和美元价值
- **自动刷新**：支持手动刷新数据功能

## 技术架构

应用采用了 **Clean Architecture** 和 **MVVM** 架构模式，主要分为以下几层：

### 数据层 (Data Layer)
- **Repository**：负责协调数据源，提供数据给上层
- **DataSource**：负责从不同来源获取数据（本地文件、网络等）
- **Models**：定义数据模型和实体类

### 表现层 (Presentation Layer)
- **Activity/Fragment**：负责 UI 展示和用户交互
- **Adapter**：处理 RecyclerView 数据绑定

### 领域层 (Domain Layer)
- **UseCase**：包含业务逻辑（隐式实现）

## 技术栈

- **Kotlin**：主要开发语言
- **Coroutines & Flow**：处理异步操作和响应式编程
- **Hilt**：依赖注入框架
- **Kotlinx.Serialization**：JSON 解析
- **Coil**：图片加载库
- **AndroidX**：Android Jetpack 组件
- **Material Design**：UI 设计规范

## 项目结构

```
app/src/main/
├── kotlin/com/example/myapplication/
│   ├── data/
│   │   ├── error/
│   │   │   └── WalletError.kt
│   │   ├── repository/
│   │   │   ├── WalletRepository.kt
│   │   │   └── WalletRepositoryImpl.kt
│   │   └── source/
│   │       ├── WalletDataSource.kt
│   │       └── WalletDataSourceImpl.kt
│   ├── di/
│   │   └── AppModule.kt
│   ├── models/
│   │   └── WalletModels.kt
│   ├── util/
│   │   └── ImageLoader.kt
│   ├── CurrencyAdapter.kt
│   ├── MainActivity.kt
│   └── WalletApplication.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   └── item_currency.xml
│   └── ...
└── assets/
    ├── currencies.json
    ├── live-rates.json
    └── wallet-balance.json
```

## 错误处理

应用实现了全面的错误处理机制，包括：

- **文件不存在错误**：处理资产文件缺失情况
- **解析错误**：处理 JSON 解析失败情况
- **数据无效错误**：处理数据格式或内容不正确情况
- **网络错误**：处理网络连接问题

## 数据流

1. 应用启动时，`MainActivity` 通过 `WalletRepository` 请求钱包状态
2. `WalletRepositoryImpl` 委托 `WalletDataSource` 获取数据
3. `WalletDataSourceImpl` 从本地资产文件读取 JSON 数据并解析
4. 解析后的数据通过 Flow 流回 `MainActivity`
5. `MainActivity` 更新 UI 显示

## 未来改进

- 添加实时网络数据获取功能
- 实现交易功能
- 添加更多加密货币支持
- 实现数据缓存机制
- 添加用户认证功能
- 支持多语言

## 安装与运行

1. 克隆仓库
2. 使用 Android Studio 打开项目
3. 构建并运行应用

## 发布新版本

要发布新版本，请按照以下步骤操作：

1. 更新版本号（在 `app/build.gradle` 中）
2. 提交更改并创建新的标签：
   ```bash
   git tag -a v1.0.0 -m "版本 1.0.0"
   git push origin v1.0.0
   ```
3. GitHub Actions 将自动构建并发布 APK

## 许可证

[MIT License](LICENSE) 