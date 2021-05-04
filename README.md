### How To Use

-----------------------------------------------



#### xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clipChildren="false"
    tools:context=".MainActivity">

    <com.ssong_develop.selectableshadowpositionview.SelectableShadowPositionView
        android:layout_width="200dp"
        android:layout_height="200dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@color/black"
            android:textSize="12sp"
            android:text="SelectableShadowPositionView"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"/>
    </com.ssong_develop.selectableshadowpositionview.SelectableShadowPositionView>
</androidx.constraintlayout.widget.ConstraintLayout>

in parentView of SelectableShadowPositionView , must clipChildren check 'false'!!!!
if you do not this, it can't show shadow to your view
and it will provide default 'blur Radius' or 'blurStrokeWidth' etc....
You can controll attributes like
topOffset 
bottomOffset
startOffset
endOffset
shadowStartY
shadowColor
shadowStrokeWidth
cornerRadius
borderHeight
borderColor

enableShadow
enableBorder
enableShadowTop
enableShadowBottom
enableShadowStart
enableShadowEnd
```

-----------------------------------------------------------------------------------------------------------------------------------------------------------



### Download

- MinSDK = 26

------------------------------------------------------------------------------------------------------------------------



### Project-level build.gradle

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### Module-level build.gradle

```
	dependencies {
	        implementation 'com.github.SSong-develop:SelectableShadowPositionsView:1.0.1'
	}
```

