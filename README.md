A project that is able to get the result of calculation only by scanning a Calculation formula.(Without using any cellula of your brain,just scan a formula by your phone!Is it cool?!)

It is based on https://github.com/Mathpix/android-sample,it need two core modules:Symja library and Mathpix.
The first libreary is a Java Symbolic Math System,you can find more on https://github.com/axkr/symja_android_library, and I also use it to do job about calculation. Mathpix is able to translate a image to lateX.Find more on https://mathpix.com/.

What i do is building a bridge between them which contain lot of work of parsing String into which Symja can identify.
 
