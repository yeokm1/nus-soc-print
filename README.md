nus-soc-print
=============

You can download this app from the Play Store here.

https://play.google.com/store/apps/details?id=com.yeokm1.nussocprintandroid

This Android application prints Microsoft Office documents (experimental) and PDF files to UNIX printers in NUS School of Computing via the usage of SSH commands. Custom server address and printers can be added.

Warning: This app works even outside the vicinity of the NUS network. So don't be too trigger happy with the print button.


<b>Main Features:</b>

1. Print PDF, DOC, DOCX, PPT, PPTX and ODT files
2. Remaining quota check
3. Print Status Check
4. Print to custom printer
5. Page formatting for pdf files
6. Can share files to this app via file browser applications


<b>Things to take note:</b>

1. Check the printer status first before printing

2. Don't hit multiple actions more than once, every single click corresponds to a new request.

3. Avoid using 3G or unstable connection when printing, the SSH library may go haywire over this. A Wifi network or better yet, the NUS's Wifi network is always preferred.

4. If the force disconnect button does not work, force a closure of the app by removing it from the recent task list. Then open it again.

5. Since this app is still new, bugs are to be expected. Inform me if you do encounter them.


<b>Bugs so far: </b>

1. Page border for M2 does not work

2. Pages/Sheet dropdown menu does not appear properly on Android 4.3 devices 


A high level write-up can be viewed on my blog here http://yeokhengmeng.com/2013/03/nus-soc-print-android-app


<b>Workings: How are things actually done?</b>

1. Document is uploaded via SSH to a folder called socPrint in your UNIX account

2. Document converted to PDF if necessary via external download of my docs-to-pdf-converter program. (https://github.com/yeokm1/docs-to-pdf-converter)

Method 1:

3a. PDF File converted to PostScript (pdftops)

3b. Postscript file formatted according to requirements (psnup)

Method 2:

3a. PDF formatted using nup-pdf java program from ruby pdf

3b. PDF File converted to PostScript (pdftops)

Method 3:

3a. PDF formatted using Multivalent tools Impose java program

3b. PDF File converted to PostScript (pdftops)

Finally: Send postscript file to print queue (uses the lpr command)


<b>Quick Guide and Recommendation:</b>

"Reset Connection" button: Sometimes the SSH connection may throw up some issues, hit this button. It also halts any active connection.

"Clear Server Cache" button: To remove unwanted files in the socPrint folder. 



<b>Credits to:</b>

ActionBar Sherlock to support older Android versions http://actionbarsherlock.com/

PreferenceListFragment from https://github.com/artiomchi/AndroidExtensions/blob/master/AndroidExtensions/src/main/java/org/flexlabs/androidextensions/preference/PreferenceListFragment.java

Jsch Java SSH2 library http://www.jcraft.com/jsch/

Android File Dialog: http://code.google.com/p/android-file-dialog/

Icons by Jack Cai: http://findicons.com/icon/175958/print

A Stackoverflow user for SSH code: http://stackoverflow.com/questions/2405885/any-good-jsch-examples

nup-pdf http://blog.rubypdf.com/2007/08/24/how-to-make-n-up-pdf-with-free-software/

Multivalent tools http://multivalent.sourceforge.net/Tools/pdf/Impose.html 

