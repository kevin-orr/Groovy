
# Java 8 streams compared to Groovy

There's a great [DZone article on Java 8 streams](https://dzone.com/articles/a-java-8-streams-cookbook?edition=268944&utm_source=Daily%20Digest&utm_medium=email&utm_campaign=dd%202017-02-14) which gives the reader 
plenty of examples on how to use the new stream collections.

The great thing about Groovy though is that all these examples work right out of 
the box - no need to change anything - it just works!

So, I thought I'd re-write the examples in that article but more Groovy like.

For example, here's a snippet that finds winners of Tour that were 3500KM or less:
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
Here's my attempt at using Groovy:
```java
// Filter and Map -
def winnersOfToursLessThan3500km = tdfWinners
    .grep { it.lengthKm < 3500 }
    .collect { it.name }
// Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]
println("Winners of Tours Less than 3500km - $winnersOfToursLessThan3500km")
        
```

I like the streams stuff in Java 8 but I have to admit I like using Groovy 
when it comes to messing around with collections. The beauty of Groovy is
that you can have you cake and eat it!! Happy days!!


Here's the output when I run it:

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
