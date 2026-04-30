# TreeSelector

简体中文 | [English](#english)

一款 Android 底部弹出的树形选择器，支持单选/多选、互斥选择、自定义样式等功能。

---

## English

An Android bottom-sheet tree selector with support for single/multiple selection, exclusive selection, and custom styling.

### Preview

![preview](./assets/preview.gif)

### Features | 功能特点

- 底部弹出的 BottomSheetDialog 样式 | Bottom-sheet dialog style based on `BottomSheetDialogFragment`
- 支持树形结构数据（父节点 + 子节点） | Tree structure data support (parent + child nodes)
- 支持单选和多选模式 | Single and multiple selection modes
- 支持互斥选择 | Exclusive selection support
- 5种 Dialog 样式 | 5 Dialog styles: Normal, Bottom, Show, Unverify, BottomAndUnverify
- 支持自定义布局样式 | Fully customizable layout
- 支持清空操作回调 | Clear/Reset functionality with callback

---

## Installation | 安装

### Gradle

**Step 1.** Add it in your root `settings.gradle.kts` at the end of repositories:

在根 `settings.gradle.kts` 中添加仓库地址：

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2.** Add the dependency in your app's `build.gradle.kts`:

在 app 的 `build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.github.NGC185air110:TreeSelector:1.6.2")
}
```

---

## Quick Start | 快速开始

### 1. Define Data Model | 定义数据模型

继承 `DlcTree` 抽象类，实现 `toChildDlcTree()` 方法返回子节点列表：

Extend `DlcTree` abstract class and implement `toChildDlcTree()`:

```kotlin
class AddressModel : DlcTree() {

    var id: Int? = null
    var name: String? = null
    var data: ArrayList<AddressModel>? = null  // 子节点数据 / children

    override fun toString(): String {
        return name ?: ""
    }

    override fun toChildDlcTree(): Any? {
        return data
    }
}
```

### 2. Build Data | 构建数据

```kotlin
var array = ArrayList<AddressModel>()
for (parentIndex in 0..2) {
    var children = ArrayList<AddressModel>()
    for (childIndex in 0..5) {
        children.add(AddressModel().apply {
            id = Random.nextInt()
            name = "父节点$parentIndex -> 子节点$childIndex"
            // Parent $parentIndex -> Child $childIndex
        })
    }
    array.add(AddressModel().apply {
        id = Random.nextInt()
        name = "父节点$parentIndex"  // Parent $parentIndex
        data = children
    })
}
```

### 3. Show Selector | 显示选择器

```kotlin
SelectDialog<AddressModel>().builder {
    data = array
    isTreeArray = true      // 开启树形结构 / enable tree structure
    spanCount = 3           // 每行显示几个 / items per row
    maximum = 10            // 最大选中数量 / max selection count

    BackchickList = { selectedList ->
        // 回调选中的数据 / callback with selected data
        selectedList.forEach { model ->
            println("${model.name}, ${model.id}")
        }
    }
}.show(supportFragmentManager, "selectDialog")
```

---

## API Reference | API 参考

### Dialog Styles | Dialog 样式

| 样式 Style | 说明 Description |
|------------|-----------------|
| `NORMAL` | 普通模式，顶栏有取消/标题/确定按钮 / Normal mode with top cancel/title/confirm |
| `BOTTOM` | 底部模式，底栏有清空/确认按钮 / Bottom mode with clear/confirm buttons |
| `SHOW` | 纯展示模式，仅显示数据，无按钮 / Display only, no buttons |
| `UNVERIFY` | 无确认按钮，点击直接返回 / No confirm button, returns on item click |
| `BOTTOMANDUNVERIFY` | 底部按钮 + 点击直接返回 / Bottom buttons + click to return |

### Main Properties | 主要属性

| 属性 Property | 类型 Type | 默认值 Default | 说明 Description |
|---------------|-----------|----------------|-----------------|
| `data` | ArrayList<T> | - | 树形数据 / Tree data |
| `isTreeArray` | Boolean | true | 是否开启树形结构 / Enable tree structure |
| `mutuallyExclusive` | Boolean | false | 是否互斥选择 / Enable exclusive selection |
| `mutuallyExclusiveToastValue` | String | "不能同时选择罐车和非罐车" | 互斥提示语 / Exclusive warning message |
| `maximum` | Int | Int.MAX_VALUE | 最大选中数量 / Max selection count |
| `spanCount` | Int | 3 | 每行显示数量 / Items per row |
| `selectCancelable` | Boolean | false | 是否可以下滑关闭 / Enable swipe to dismiss |
| `alwaysListNotNull` | Boolean | true | 是否必须选中至少一个 / Require at least one selection |
| `selectBold` | Boolean | false | 选中文字是否加粗 / Bold selected text |
| `dialogStyle` | DialogStyle | NORMAL | Dialog 样式 / Dialog style |
| `maximumHeight` | Float | 0F | item 等高高度 / Fixed item height |
| `itemPaddingAbout` | Float | 0F | item 左右内边距 / Item horizontal padding |
| `tvDeleteIsShow` | Boolean | false | 是否显示清空按钮 / Show clear button |

### Style Customization | 样式自定义

| 属性 Property | 说明 Description |
|---------------|-----------------|
| `pitchOn` | 选中时背景 drawable / Selected background drawable |
| `pitchOff` | 未选中时背景 drawable / Unselected background drawable |
| `tvColorOn` | 选中时文字颜色 / Selected text color |
| `tvColorOff` | 未选中时文字颜色 / Unselected text color |
| `itemMarginEnd` | item 右侧间距 / Item right margin |
| `itemMarginBottom` | item 下方间距 / Item bottom margin |
| `rvPaddingStart/Top/End/Bottom` | RecyclerView 内边距 / RecyclerView padding |

### Callbacks | 回调接口

```kotlin
// 确认按钮回调 / Confirm callback
var BackchickList: ((ArrayList<T>) -> Unit)? = null

// 自定义确认按钮 / Custom confirm button
var getConfirm: ((TextView) -> Unit)? = null

// 自定义标题 / Custom title
var getTitle: ((TextView) -> Unit)? = null

// 自定义取消按钮 / Custom cancel button
var getCancel: ((TextView) -> Unit)? = null

// 自定义底部确认按钮 / Custom bottom confirm button
var getConfirmBottom: ((TextView) -> Unit)? = null

// 清空按钮回调 / Clear button callback
var clearBackChick: (() -> Unit)? = null
```

---

## Examples | 示例

### 基础树形多选 | Basic Tree Multiple Selection

```kotlin
SelectDialog<AddressModel>().builder {
    data = array
    isTreeArray = true
    spanCount = 3
    maximum = 10
    BackchickList = { list ->
        textView.text = list.joinToString { it.name }
    }
}.show(supportFragmentManager, "treeDialog")
```

### 互斥选择 | Exclusive Selection

```kotlin
SelectDialog<AddressModel>().builder {
    data = array
    isTreeArray = true
    mutuallyExclusive = true
    mutuallyExclusiveToastValue = "同类选项不能同时选择"
    maximum = 1
    spanCount = 3
    BackchickList = { list ->
        textView.text = list.firstOrNull()?.name ?: ""
    }
}.show(supportFragmentManager, "exclusiveDialog")
```

### 纯展示模式 | Display Only Mode

```kotlin
SelectDialog<AddressModel>().builder {
    data = array
    dialogStyle = DialogStyle.SHOW
    isTreeArray = true
    maximum = Int.MAX_VALUE
}.show(supportFragmentManager, "showDialog")
```

### 点击即选模式 | Click-to-Select Mode

```kotlin
SelectDialog<AddressModel>().builder {
    data = array
    dialogStyle = DialogStyle.UNVERIFY
    maximum = 1
    isTreeArray = false
    BackchickList = { list ->
        textView.text = list.firstOrNull()?.name ?: ""
    }
}.show(supportFragmentManager, "singleDialog")
```

### 自定义样式 | Custom Style

```kotlin
SelectDialog<AddressModel>().builder {
    data = array
    isTreeArray = false
    spanCount = 1
    tvColorOff = R.color.teal_200
    tvColorOn = R.color.black
    selectBold = true
    getConfirmBottom = {
        it.text = "自定义确定"  // Custom Confirm
        it.setPadding(50, 20, 50, 20)
    }
    BackchickList = { list ->
        Toast.makeText(this@MainActivity, list[0].name, Toast.LENGTH_LONG).show()
    }
}.show(supportFragmentManager, "customDialog")
```

### 带清空功能 | With Clear Function

```kotlin
SelectDialog<AddressModel>().builder {
    data = array
    dialogStyle = DialogStyle.BOTTOM
    tvDeleteIsShow = true
    clearBackChick = {
        Toast.makeText(this@MainActivity, "已清空", Toast.LENGTH_LONG).show()
    }
    BackchickList = { list ->
        textView.text = list.joinToString { it.name }
    }
}.show(supportFragmentManager, "clearDialog")
```

---

## License | 许可证

MIT License

Copyright (c) 2022 dinglicheng

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.