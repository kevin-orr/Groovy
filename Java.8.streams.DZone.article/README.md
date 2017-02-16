
# Java 8 streams - the Groovy way...

There's a great [DZone article on Java 8 streams](https://dzone.com/articles/a-java-8-streams-cookbook?edition=268944&utm_source=Daily%20Digest&utm_medium=email&utm_campaign=dd%202017-02-14) which gives the reader 
a myriad of examples on just how to use the new Java 8 streams.


The great thing about Groovy though, all these examples work almost right out of 
the box (well, ok, you'll have to change lambdas to Closures) - but the point is, I suppose, if 
you're using Java 8, the you have a choice when using Groovy - either use the new Stream (with a 
little sprinkle of Groovy magic on top), or go ahead and use the Groovy coolections. 
You've got both options available to you as a Groovy developer - that's a nice buffer to have 
when trying to solve tricky problems - its all good!!


Anyways, so, I thought I'd re-write the examples from the article but more 
Groovy in nature (oh, here's a disclaimer - I'm relatively new to Groovy so I'm 
sure there's plenty of better and more efficient ways of doing it in Groovy).

This was a nice way to learn more about streams while also learning some more about Groovy.


So, for example, here's a snippet from the article that finds winners of Tours that were 3500 km or less:

```java
// Filter and Map -
List<String> winnersOfToursLessThan3500km = tdfWinners
    .stream()
    .filter(d -> d.getLengthKm() < 3500) // Separate out Tours less than 3500km
    .map(Winner::getName) // Get names of winners
    .collect(toList()); // Return a list
// Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]        
System.out.println("Winners of Tours Less than 3500km - " + winnersOfToursLessThan3500km);

```

and here's an attempt at using Groovy to achieve the same result:

```java
// Filter and Map -
def winnersOfToursLessThan3500km = tdfWinners
    .grep { it.lengthKm < 3500 }
    .collect { it.name }
// Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]
println("Winners of Tours Less than 3500km - $winnersOfToursLessThan3500km")
        
```


I like the streams stuff in Java 8 but, if I'm honest, I think I prefer using Groovy 
when it comes to messing around with collections.  
The beauty of Groovy though, you can have your cake and eat it!! Happy days!!


When I ran the code here's the output:

```
Hi im final string                                                                                                                                      
Hi im effectively final string                                                                                                                          
Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]                                        
Bye, Bye...                                                                                                                                             
Winners of Tours Greater than 3500km - [Óscar Pereiro, Alberto Contador, Carlos Sastre, Andy Schleck, Vincenzo Nibali, Chris Froome]                    
winnerObjectsOfToursLessThan3500kmLimit2 [Alberto Contador, Cadel Evans]                                                                                
firstTwoWinnersOfToursLessThan3500km - [Alberto Contador, Cadel Evans]                                                                                  
distinctTDFWinners - [Óscar Pereiro, Alberto Contador, Carlos Sastre, Andy Schleck, Cadel Evans, Bradley Wiggins, Chris Froome, Vincenzo Nibali]        
numberOfDistinceWinners - 8                                                                                                                             
skipEveryOtherTDFWinner - [Óscar Pereiro, Carlos Sastre, Andy Schleck, Bradley Wiggins, Vincenzo Nibali, Chris Froome]                                  
skipEveryOtherTDFWinner #2 - [Carlos Sastre, Alberto Contador, Andy Schleck, Cadel Evans, Bradley Wiggins, Chris Froome, Vincenzo Nibali, Chris Froome, Chris Froome]                                                                                                                                           
skipEveryOtherTDFWinner #3 - [Carlos Sastre, Alberto Contador, Andy Schleck, Cadel Evans, Bradley Wiggins, Chris Froome, Vincenzo Nibali, Chris Froome, Chris Froome]                                                                                                                                           
mapWinnerYearNamesToList [2006 -  Óscar Pereiro, 2007 -  Alberto Contador, 2008 -  Carlos Sastre, 2009 -  Alberto Contador, 2010 -  Andy Schleck, 2011 -  Cadel Evans, 2012 -  Bradley Wiggins, 2013 -  Chris Froome, 2014 -  Vincenzo Nibali, 2015 -  Chris Froome, 2016 -  Chris Froome]                      
mapWinnerNameLengthToList [13, 16, 13, 16, 12, 11, 15, 12, 15, 12, 12]                                                                                  
winner2012 - Bradley Wiggins                                                                                                                            
winner2012 - Bradley Wiggins                                                                                                                            
winnerYear2014 - Vincenzo Nibali                                                                                                                        
totalDistance - 38767                                                                                                                                   
shortestYear - 3360                                                                                                                                     
shortestYear - 3360                                                                                                                                     
longestYear - 3661                                                                                                                                      
fastestTDF - Alberto Contador                                                                                                                           
fastestTDF - 39.2276559248                                                                                                                              
namesVsWinner - [Óscar Pereiro:[Óscar Pereiro], Alberto Contador:[Alberto Contador, Alberto Contador], Carlos Sastre:[Carlos Sastre], Andy Schleck:[Andy Schleck], Cadel Evans:[Cadel Evans], Bradley Wiggins:[Bradley Wiggins], Chris Froome:[Chris Froome, Chris Froome, Chris Froome], Vincenzo Nibali:[Vincenzo Nibali]]                                                                                                                                            
allTDFWinnersTeams Caisse d'Epargne–Illes Balears, Discovery Channel, Team CSC, Astana, Team Saxo Bank, BMC Racing Team, Team Sky, Team Sky, Astana, Team Sky, Team Sky                                                                                                                                         
winnersByNationality - [Spain:[Óscar Pereiro, Alberto Contador, Carlos Sastre, Alberto Contador], Luxembourg:[Andy Schleck], Australia:[Cadel Evans], Great Britain:[Bradley Wiggins, Chris Froome, Chris Froome, Chris Froome], Italy:[Vincenzo Nibali]]                                                       
winsByNationalityCounting - [Spain, 4, Luxembourg, 1, Australia, 1, Great Britain, 4, Italy, 1] 
```
