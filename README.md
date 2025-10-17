# 待办事项应用 (Todo App)

一个功能完整的Android待办事项管理应用，使用Jetpack Compose构建，支持添加、编辑、删除待办事项，以及设置重复提醒。

## 功能特性

### 核心功能
- ✅ 添加待办事项
- ✅ 编辑待办事项
- ✅ 删除待办事项
- ✅ 标记完成/未完成
- ✅ 查看已完成和未完成的待办事项

### 重复功能
- 🔄 一次性待办事项
- 🔄 每日重复
- 🔄 每周重复

### 通知功能
- 🔔 待办事项到期前1小时自动提醒
- 🔔 支持系统通知
- 🔔 自动取消已完成事项的通知

## 技术栈

- **UI框架**: Jetpack Compose
- **架构**: MVVM + Repository模式
- **依赖注入**: Dagger Hilt
- **数据库**: Room (SQLite)
- **导航**: Navigation Compose
- **通知**: WorkManager + NotificationCompat
- **日期选择**: Material Dialogs

## 项目结构

```
app/src/main/java/com/example/todoapp/
├── data/                    # 数据层
│   ├── TodoItem.kt         # 数据模型
│   ├── TodoDao.kt          # 数据访问对象
│   ├── TodoDatabase.kt     # 数据库配置
│   └── Converters.kt       # 类型转换器
├── repository/             # 仓库层
│   └── TodoRepository.kt   # 数据仓库
├── ui/                     # UI层
│   ├── screens/           # 界面
│   │   ├── TodoListScreen.kt
│   │   └── AddEditTodoScreen.kt
│   ├── components/        # 组件
│   │   └── TodoItemCard.kt
│   ├── navigation/        # 导航
│   │   └── TodoNavigation.kt
│   ├── viewmodel/         # 视图模型
│   │   ├── TodoViewModel.kt
│   │   └── AddEditTodoViewModel.kt
│   └── theme/             # 主题
├── notification/          # 通知功能
│   ├── TodoNotificationWorker.kt
│   └── NotificationHelper.kt
├── di/                    # 依赖注入
│   └── DatabaseModule.kt
├── MainActivity.kt        # 主活动
└── TodoApplication.kt     # 应用程序类
```

## 安装和运行

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Android SDK 24 或更高版本
- Kotlin 1.9.10 或更高版本

### 构建步骤

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击运行按钮

### 权限说明

应用需要以下权限：
- `POST_NOTIFICATIONS`: 发送通知
- `SCHEDULE_EXACT_ALARM`: 安排精确的闹钟提醒
- `USE_EXACT_ALARM`: 使用精确闹钟

## 使用说明

### 添加待办事项
1. 点击主界面右下角的"+"按钮
2. 填写标题（必填）
3. 填写描述（可选）
4. 选择截止日期和时间
5. 选择重复类型（一次性/每日/每周）
6. 点击"保存"

### 管理待办事项
- **标记完成**: 点击待办事项左侧的复选框
- **编辑**: 点击待办事项右侧的编辑图标
- **删除**: 点击待办事项右侧的删除图标
- **查看已完成**: 点击顶部栏的完成图标切换显示

### 重复功能
- **一次性**: 任务只执行一次
- **每日**: 任务每天重复，完成当前任务后自动创建下一天的任务
- **每周**: 任务每周重复，完成当前任务后自动创建下一周的任务

### 通知功能
- 应用会在待办事项到期前1小时发送通知提醒
- 完成待办事项后，相关通知会自动取消
- 删除待办事项时，相关通知也会被取消

## 开发说明

### 数据库设计
使用Room数据库存储待办事项，主要字段包括：
- `id`: 主键，自增
- `title`: 标题
- `description`: 描述
- `dueDate`: 截止日期
- `isCompleted`: 是否完成
- `repeatType`: 重复类型
- `createdAt`: 创建时间
- `lastModified`: 最后修改时间

### 重复逻辑
重复任务的实现逻辑：
1. 当用户完成一个重复任务时，系统会自动创建下一个周期的任务
2. 新任务的截止日期会根据重复类型自动计算（每日+1天，每周+7天）
3. 新任务会自动安排通知提醒

### 通知机制
使用WorkManager实现后台通知：
1. 创建待办事项时安排通知
2. 通知在截止时间前1小时触发
3. 任务完成或删除时取消通知
4. 支持精确的闹钟提醒

## 许可证

本项目采用MIT许可证，详情请查看LICENSE文件。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 联系方式

如有问题或建议，请通过GitHub Issues联系。
