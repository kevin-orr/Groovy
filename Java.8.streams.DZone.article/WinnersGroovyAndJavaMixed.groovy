import java.time.Duration
import java.util.*

import static java.util.stream.Collectors.toList
import static java.util.stream.Collectors.*

/**
 * Reworked with min changes to work with Groovy...
 */
class WinnersGroovyAndJavaMixed {

    int year
    String nationality
    String name
    String team
    int lengthKm
    Duration winningTime
    int stageWins
    int daysInYellow

    // no need for public modifier - public is the default access modifier for methods if not specified
    // also using Groovy's list [...]
    static final List<Winner> tdfWinners = [
            new Winner(year:2006, nationality:'Spain', name:'Óscar Pereiro', team:"Caisse d'Epargne–Illes Balears", lengthKm:3657, winningTime:Duration.parse('PT89H40M27S'), daysInYellow:8),
            new Winner(year:2007, nationality:'Spain', name:'Alberto Contador', team:'Discovery Channel', lengthKm:3570, winningTime:Duration.parse('PT91H00M26S'), daysInYellow:4),
            new Winner(year:2008, nationality:'Spain', name:'Carlos Sastre', team:'Team CSC', lengthKm:3559, winningTime:Duration.parse('PT87H52M52S'), daysInYellow:5),
            new Winner(year:2009, nationality:'Spain', name:'Alberto Contador', team:'Astana', lengthKm:3459, winningTime:Duration.parse('PT85H48M35S'), daysInYellow:7),
            new Winner(year:2010, nationality:'Luxembourg', name:'Andy Schleck', team:'Team Saxo Bank', lengthKm:3642, winningTime:Duration.parse('PT91H59M27S'), daysInYellow:12),
            new Winner(year:2011, nationality:'Australia', name:'Cadel Evans', team:'BMC Racing Team', lengthKm:3430, winningTime:Duration.parse('PT86H12M22S'), daysInYellow:2),
            new Winner(year:2012, nationality:'Great Britain', name:'Bradley Wiggins', team:'Team Sky', lengthKm:3496, winningTime:Duration.parse('PT87H34M47S'), daysInYellow:14),
            new Winner(year:2013, nationality:'Great Britain', name:'Chris Froome', team:'Team Sky', lengthKm:3404, winningTime:Duration.parse('PT83H56M20S'), daysInYellow:14),
            new Winner(year:2014, nationality:'Italy', name:'Vincenzo Nibali', team:'Astana', lengthKm:3661, winningTime:Duration.parse('PT89H59M06S'), daysInYellow:19),
            new Winner(year:2015, nationality:'Great Britain', name:'Chris Froome', team:'Team Sky', lengthKm:3360, winningTime:Duration.parse('PT84H46M14S'), daysInYellow:16),
            new Winner(year:2016, nationality:'Great Britain', name:'Chris Froome', team:'Team Sky', lengthKm:3529, winningTime:Duration.parse('PT89H04M48S'), daysInYellow:14 )
    ]

    public static void main(String...args) {

        // using Java 8 streams...Filter and Map -
        List<String> winnersOfToursLessThan3500km = tdfWinners
                .stream()
                .filter({d -> d.lengthKm < 3500}) // Separate out Tours less than 3500km
                .map({winner -> winner.name}) // Get names of winners
                .collect(toList()) // Return a list
        // Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]
        System.out.println("Winners of Tours Less than 3500km - " + winnersOfToursLessThan3500km)
        // using Groovys collections...
        def winnersOfToursLessThan3500km_groovy = tdfWinners
                .grep { winner -> winner.lengthKm < 3500 }
                .collect { winner -> winner.name }
        // Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]
        println("Winners of Tours Less than 3500km - $winnersOfToursLessThan3500km_groovy")


        List<String> winnersOfToursGreaterThan3500km = tdfWinners
                .stream()
                .filter({d -> d.lengthKm >= 3500})
                .map({winner -> winner.name})
                .collect(toList())
        // Winners of Tours Greater than 3500km - [Óscar Pereiro, Alberto Contador, Carlos Sastre, Andy Schleck, Vincenzo Nibali, Chris Froome]
        System.out.println("Winners of Tours Greater than 3500km - " + winnersOfToursGreaterThan3500km)

        def winnersOfToursGreaterThan3500km_groovy = tdfWinners
                .grep { winner -> winner.lengthKm >= 3500 }
                .collect { winner -> winner.name }
        // Winners of Tours Greater than 3500km - [Óscar Pereiro, Alberto Contador, Carlos Sastre, Andy Schleck, Vincenzo Nibali, Chris Froome]
        println("Winners of Tours Greater than 3500km - $winnersOfToursGreaterThan3500km_groovy")


        // limit -
        List<Winner> winnerObjectsOfToursLessThan3500kmLimit2 = tdfWinners
                .stream()
                .filter({d -> d.lengthKm < 3500})
                .limit(2)
                .collect(toList());
        // winnerObjectsOfToursLessThan3500kmLimit2 [Alberto Contador, Cadel Evans]
        System.out.println("winnerObjectsOfToursLessThan3500kmLimit2 " + winnerObjectsOfToursLessThan3500kmLimit2)

        def winnerObjectsOfToursLessThan3500kmLimit2_groovy = tdfWinners
                .grep { winner -> winner.lengthKm < 3500 }
                .take(2)
                .collect { winner -> winner.name }

        // winnerObjectsOfToursLessThan3500kmLimit2 [Alberto Contador, Cadel Evans]
        println("winnerObjectsOfToursLessThan3500kmLimit2 $winnerObjectsOfToursLessThan3500kmLimit2_groovy")

        List<String> firstTwoWinnersOfToursLessThan3500km = tdfWinners
                .stream()
                .filter({d -> d.lengthKm < 3500})
                .map({winner -> winner.name})
                .limit(2)
                .collect(toList());
        // firstTwoWinnersOfToursLessThan3500km - [Alberto Contador, Cadel Evans]
        System.out.println("firstTwoWinnersOfToursLessThan3500km - " + firstTwoWinnersOfToursLessThan3500km);

        def firstTwoWinnersOfToursLessThan3500km_groovy = tdfWinners
                .grep { winner -> winner.lengthKm < 3500 }
                .take(2)
                .collect { winner -> winner.name }
        // firstTwoWinnersOfToursLessThan3500km - [Alberto Contador, Cadel Evans]
        println("firstTwoWinnersOfToursLessThan3500km - $firstTwoWinnersOfToursLessThan3500km_groovy")


        // filter by distinct
        List<String> distinctTDFWinners = tdfWinners
                .stream()
                .map({winner -> winner.name})
                .distinct()
                .collect(toList())
        System.out.println("distinctTDFWinners - " + distinctTDFWinners)

        // filter by distinct - we want a new collection returned so pass mutate=false
        def distinctTDFWinners_groovy = tdfWinners
                .unique (false){ winner -> winner.name }

        println("distinctTDFWinners - $distinctTDFWinners_groovy")

        long numberOfDistinceWinners = tdfWinners
                .stream()
                .map({winner -> winner.name})
                .distinct()
                .count()
        // numberOfDistinceWinners - 8
        System.out.println("numberOfDistinceWinners - " + numberOfDistinceWinners)

        long numberOfDistinceWinners_groovy = tdfWinners
                .unique (false){ winner -> winner.name }?.size()
        // numberOfDistinceWinners - 8
        println("numberOfDistinceWinners - $numberOfDistinceWinners_groovy")

        // skip records
        List<Winner> skipEveryOtherTDFWinner = tdfWinners
                .stream()
                .skip(2)
                .collect(toList())
        // skipEveryOtherTDFWinner - [Carlos Sastre, Alberto Contador, Andy Schleck, Cadel Evans, Bradley Wiggins, Chris Froome, Vincenzo Nibali, Chris Froome, Chris Froome]
        System.out.println("skipEveryOtherTDFWinner - " + skipEveryOtherTDFWinner)

        // I think there's a 'bug' in the original article - the var's name is 'skipEveryOtherTDFWinner' which
        // suggest to me (but I'm dyslexic so what do I know) that alternative names are pulled from collection
        // but I think that var might be incorrectly named - what the code does is actually skip the first 2 items.
        // So, I'll just pull out the first two - if you compare outputs they are the same!
        def skipEveryOtherTDFWinner_groovy = tdfWinners
                .drop(2)
        // skipEveryOtherTDFWinner - [Óscar Pereiro, Carlos Sastre, Andy Schleck, Bradley Wiggins, Vincenzo Nibali, Chris Froome]
        println("skipEveryOtherTDFWinner - $skipEveryOtherTDFWinner_groovy")


        List<String> mapWinnerYearNamesToList = tdfWinners
                .stream()
                .map({winner -> "${winner.year} - ${winner.name}"})
                .collect(toList());
        // mapWinnerYearNamesToList [2006 - Óscar Pereiro, 2007 - Alberto Contador, 2008 - Carlos Sastre, 2009 - Alberto Contador, 2010 - Andy Schleck, 2011 - Cadel Evans, 2012 - Bradley Wiggins, 2013 - Chris Froome, 2014 - Vincenzo Nibali, 2015 - Chris Froome, 2016 - Chris Froome]
        System.out.println("mapWinnerYearNamesToList " + mapWinnerYearNamesToList)

        def mapWinnerYearNamesToList_groovy = tdfWinners
                .collect{ winner -> "${winner.year} - ${winner.name}" }

        println("mapWinnerYearNamesToList $mapWinnerYearNamesToList_groovy")

        List<Integer> mapWinnerNameLengthToList = tdfWinners
                .stream()
                .map({winner -> winner.name})
                .map({winner -> winner.length()})
                .collect(toList())
        // mapWinnerNameLengthToList [13, 16, 13, 16, 12, 11, 15, 12, 15, 12, 12]
        System.out.println("mapWinnerNameLengthToList " + mapWinnerNameLengthToList)

        def mapWinnerNameLengthToList_groovy = tdfWinners
                .collect { winner -> winner.name.length() }
        // mapWinnerNameLengthToList [13, 16, 13, 16, 12, 11, 15, 12, 15, 12, 12]
        println("mapWinnerNameLengthToList $mapWinnerNameLengthToList_groovy")


        // matching - allMatch, noneMatch
        Optional<Winner> winner2012 = tdfWinners
                .stream()
                .filter({winner -> winner.name.contains('Wiggins')})
                .findAny();
        // winner2012 - Bradley Wiggins
        System.out.println("winner2012 - " + winner2012.get())

        Optional<Winner> winner2012_groovy = tdfWinners
                .grep { winner -> winner.name.contains('Wiggins')}
        // winner2012 - Bradley Wiggins
        println("winner2012 - ${winner2012_groovy.get()}")

        Optional<Integer> winnerYear2014 = tdfWinners
                .stream()
                .map({winner -> winner.year})
                .filter({x -> x == 2014})
                .findFirst();
        // winnerYear2014 - 2014
        System.out.println("winnerYear2014 - " + winnerYear2014.get())

        def winnerYear2014_groovy = tdfWinners
                .grep {winner -> winner.year == 2014}?.first()
        // winnerYear2014 - 2014
        println("winnerYear2014 - $winnerYear2014_groovy")

        // reducing - 0 --> initial value
        int totalDistance = tdfWinners
                .stream()
                .map({winner -> winner.lengthKm})
                .reduce(0, {a, b -> a + b})
        // totalDistance - 38767
        System.out.println("totalDistance - " + totalDistance)

        int totalDistance_groovy = tdfWinners
                .sum {winner -> winner.lengthKm}
        // totalDistance - 38767
        println("totalDistance - $totalDistance_groovy")

        Optional<Integer> shortestYear = tdfWinners
                .stream()
                .map({winner -> winner.lengthKm})
                .reduce({a,b -> a<b?a:b})
        // shortestYear - 3360
        System.out.println("shortestYear - " + shortestYear.get())

        def shortestYear_groovy = tdfWinners
                .min ( { a, b -> a.lengthKm <=> b.lengthKm } as Comparator )?.lengthKm
        // shortestYear - 3360
        println("shortestYear - $shortestYear_groovy")
        shortestYear_groovy = tdfWinners
                .collect { winner -> winner.lengthKm }?.min()
        println("shortestYear - $shortestYear_groovy")

        Optional<Integer> longestYear = tdfWinners
                .stream()
                .map({winner -> winner.lengthKm})
                .reduce({a,b -> a<b?b:a})
        // longestYear - 3661
        System.out.println("longestYear - " + longestYear.get())

        def longestYear_groovy = tdfWinners
                .collect { winner -> winner.lengthKm }?.max()
        // longestYear - 3661
        println("longestYear - $longestYear_groovy")

        Optional<Winner> fastestWinner = tdfWinners
                .stream()
                .min(Comparator.comparingDouble({winner -> winner.getAveSpeed()}))
        System.out.println("fastestTDF - " + fastestWinner.get())

        def fastestWinner_groovy = tdfWinners
                .min { winner -> winner.getAveSpeed() }
        println("fastestTDF - $fastestWinner_groovy")

        // shorthand
        OptionalDouble fastestTDF = tdfWinners
                .stream()
                .mapToDouble({winner -> winner.getAveSpeed()})
                .min()
        // fastestTDF - 39.0
        System.out.println("fastestTDF - " + fastestTDF.getAsDouble());

        println("fastestTDF - ${fastestWinner_groovy.getAveSpeed()}")

//        // groupingby - make a map whose keys are names
        Map<String, List<Winner>> namesVsWinner = tdfWinners
                .stream()
                .collect(groupingBy({winner -> winner.name}))
        // namesVsWinner - {Bradley Wiggins=[Bradley Wiggins], Carlos Sastre=[Carlos Sastre], Cadel Evans=[Cadel Evans], Óscar Pereiro=[Óscar Pereiro], Chris Froome=[Chris Froome, Chris Froome, Chris Froome], Andy Schleck=[Andy Schleck], Alberto Contador=[Alberto Contador, Alberto Contador], Vincenzo Nibali=[Vincenzo Nibali]}
        System.out.println("namesVsWinner - " + namesVsWinner)

        def namesVsWinner_groovy = tdfWinners
                .groupBy { winner -> winner.name }
        // namesVsWinner - {Bradley Wiggins=[Bradley Wiggins], Carlos Sastre=[Carlos Sastre], Cadel Evans=[Cadel Evans], Óscar Pereiro=[Óscar Pereiro], Chris Froome=[Chris Froome, Chris Froome, Chris Froome], Andy Schleck=[Andy Schleck], Alberto Contador=[Alberto Contador, Alberto Contador], Vincenzo Nibali=[Vincenzo Nibali]}
        println("namesVsWinner - $namesVsWinner_groovy")


        // join strings
        String allTDFWinnersTeamsCSV = tdfWinners
                .stream()
                .map({winner -> winner.team})
                .collect(joining(", "));
        // allTDFWinnersTeams Caisse d'Epargne–Illes Balears, Discovery Channel, Team CSC, Astana, Team Saxo Bank, BMC Racing Team, Team Sky, Team Sky, Astana, Team Sky, Team Sky
        System.out.println("allTDFWinnersTeams " + allTDFWinnersTeamsCSV)

        // join strings
        String allTDFWinnersTeamsCSV_groovy = tdfWinners
                .collect {winner -> winner.team }
                .join(', ')
        // allTDFWinnersTeams Caisse d'Epargne–Illes Balears, Discovery Channel, Team CSC, Astana, Team Saxo Bank, BMC Racing Team, Team Sky, Team Sky, Astana, Team Sky, Team Sky
        println("allTDFWinnersTeams $allTDFWinnersTeamsCSV_groovy")

        // grouping
        Map<String, List<Winner>> winnersByNationality = tdfWinners
                .stream()
                .collect(groupingBy({winner -> winner.nationality }))
        // winnersByNationality - {Great Britain=[Bradley Wiggins, Chris Froome, Chris Froome, Chris Froome], Luxembourg=[Andy Schleck], Italy=[Vincenzo Nibali], Australia=[Cadel Evans], Spain=[Óscar Pereiro, Alberto Contador, Carlos Sastre, Alberto Contador]}
        System.out.println("winnersByNationality - " + winnersByNationality)

        // grouping
        Map<String, List<Winner>> winnersByNationality_groovy = tdfWinners
                .groupBy { winner -> winner.nationality }
        // winnersByNationality - {Great Britain=[Bradley Wiggins, Chris Froome, Chris Froome, Chris Froome], Luxembourg=[Andy Schleck], Italy=[Vincenzo Nibali], Australia=[Cadel Evans], Spain=[Óscar Pereiro, Alberto Contador, Carlos Sastre, Alberto Contador]}
        println("winnersByNationality - $winnersByNationality_groovy")

        Map<String, Long> winsByNationalityCounting = tdfWinners
                .stream()
                .collect(groupingBy({ winner -> winner.nationality }, counting()));
        // winsByNationalityCounting - {Great Britain=4, Luxembourg=1, Italy=1, Australia=1, Spain=4}
        System.out.println("winsByNationalityCounting - " + winsByNationalityCounting)

        def winsByNationalityCounting_groovy = tdfWinners
                .groupBy { winner -> winner.nationality }
                .collect { key, value -> [key, value.size()] }
                .flatten()
        // winsByNationalityCounting - {Great Britain=4, Luxembourg=1, Italy=1, Australia=1, Spain=4}
        println("winsByNationalityCounting - " + winsByNationalityCounting_groovy)
    }


    double getAveSpeed() {
        return (lengthKm / (winningTime.getSeconds() / 3600) )
    }

    @Override
    public String toString() {
        return name;
    }
}
