
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
