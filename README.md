<img src="https://github.com/Pedrohmv/ReceiptView/blob/master/images/receiptprint.png">

##Setup

Add Jitpack repository to your root build.gradle
```gradle
    allprojects {
        repositories {
	    ...
	    maven { url 'https://jitpack.io' }
	}
    }
```
Add the dependecy
```gradle
    dependencies {
        implementation 'com.github.Pedrohmv:ReceiptView:1.0.0'
    }
```