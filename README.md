A Java library to convert a sequence of HTTP Server Sent Events (SSE) into a 
reactive stream. Exposes methods to retrieve an RxJava Observable or Flowable 
from the sequence of events.

Once we retrieve an Observable / Flowable, the SSEs can be manipulated using 
standard reactive stream methods, eg. map, filter, flatMap, zip, window, scan, 
and so on.

For usage, please browse through the sample programs in the sample directory.

To run a sample program, edit the pom.xml if necessary, adding the appropriate
class name withh a main method. Then run:

`mvn exec:java`
