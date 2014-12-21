nus-soc-print
=============

An Android application that prints office documents and PDF files to Unix printers in NUS School Of Computing via SSH. The iOS version exists [here](https://github.com/yeokm1/nus-soc-print-ios/).

This app has been approved on the [Play Store](https://play.google.com/store/apps/details?id=com.yeokm1.nussocprintandroid).

##Features
1. Print PDF, DOC, DOCX, PPT, PPTX and ODT files
2. Page range to be printed
3. Remaining quota check
4. Print Status Check
5. Page formatting for documents
6. Accepts file sharing from other apps
7. Deleting of existing job in print queue
8. Runs on Android 2.3 and up for maximum compatibility

##Tools used
1. Java 8 Update 25 64 bit SDK
2. Android 5.0.1 SDK
3. Android Studio 1.0.2
4. Jsch SSH library (included)
5. Flurry Analytics (included)
6. Crashlytics (included)  
7. Docs to PDF converter (included). From [another project of mine](https://github.com/yeokm1/docs-to-pdf-converter).
8. Craft Support Email Intent (included). From [another project of mine](https://github.com/yeokm1/craft-support-email-intent).

##References
1. [PreferenceListFragment](https://github.com/artiomchi/AndroidExtensions/blob/master/AndroidExtensions/src/main/java/org/flexlabs/androidextensions/preference/PreferenceListFragment.java)
2. [SSH example code](http://stackoverflow.com/questions/2405885/any-good-jsch-examples)
3. [nup_pdf PDF formatting library](http://blog.rubypdf.com/2007/08/24/how-to-make-n-up-pdf-with-free-software/)
4. [Multivalent PDF formatting library](http://multivalent.sourceforge.net/Tools/pdf/Impose.html)
5. [Merge 2 Git repos](http://blog.caplin.com/2013/09/18/merging-two-git-repositories/)
