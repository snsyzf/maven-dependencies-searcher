### Maven Dependencies Searcher

Query search for maven dependencies.

-----

|                   |               |                                               |
|  ----             | ----          | ----                                          |
| Or                |	OR or -	    | g:junit OR a:junit                            |
| And               |	AND or +	| g:junit AND a:junit                           |
| Group             |	g:	        | g:junit                                       |
| Artifact          |	a:	        | a:junit                                       |
| Version           |	v:	        | v:4.11                                        |
| Packaging         |	p:	        | p:jar or p:war                                |
| Classifier        |	l:	        | l:sources or l:javadocs                       |
| Class name        |	c:	        | c:JUnit4                                      |
| Full Class name   |	fc:	        | fc:org.sonatype.nexus                         |
| SHA-1             | 	1:	        | 1:2973d150c0dc1fefe998f834810d68f278ea58ec    |


#### Change logs

##### 1.0.5
+ Add page size
+ Add download source support

##### 1.0.3
+ Supported 2019.2
+ Optimize network requests

##### 1.0.2
+ Remove useless dependencies
+ Add more loading
+ Add copy option
+ Add search options

##### 1.0.1
+ Remove jide library
+ Fix Expand bug
+ Fix Collapse bug

##### 1.0.0
+ Dependent search
+ Dependent download
