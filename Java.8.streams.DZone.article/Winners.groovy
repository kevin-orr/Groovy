import java.time.Duration

import static java.util.stream.Collectors.toList;

import static java.util.stream.Collectors.*;



/**
 * Created by kevinorr on 14/02/17.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kevinorr
 */
public class Winner {
    /**
    notice no ctor and members not marked as private - http://www.groovy-lang.org/differences.html#_package_scope_visibility
    */
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

    static void main(String...args) {
        
        def finalString = 'final string'
        def effectivelyFinalString = 'effectively final string'
        def r = {
            System.out.println("Hi im $finalString")
            System.out.println("Hi im $effectivelyFinalString")
            System.out.println("Bye, Bye...")
        }
        // so I can pass in a closure and it will behave as Runnable
        new Thread(r).start()

        // now 'translate' to Groovy...
        // Filter and Map -
        def winnersOfToursLessThan3500km = tdfWinners
                .grep { it.lengthKm < 3500 }
                .collect { it.name }
        // Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]
        println("Winners of Tours Less than 3500km - $winnersOfToursLessThan3500km")
        // using Groovy's String interpolation here --^^^^^^^^^^^^^^^^^^^^^^^^^^

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        def winnersOfToursGreaterThan3500km = tdfWinners
                .grep { it.lengthKm >= 3500 }
                .collect { it.name }
        // Winners of Tours Greater than 3500km - [Óscar Pereiro, Alberto Contador, Carlos Sastre, Andy Schleck, Vincenzo Nibali, Chris Froome]
        println("Winners of Tours Greater than 3500km - $winnersOfToursGreaterThan3500km")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'
        // limit -
        def winnerObjectsOfToursLessThan3500kmLimit2 = tdfWinners
                .grep { it.lengthKm < 3500 }
                .collect { it.name }
                .take(2)

        // winnerObjectsOfToursLessThan3500kmLimit2 [Alberto Contador, Cadel Evans]
        println("winnerObjectsOfToursLessThan3500kmLimit2 $winnerObjectsOfToursLessThan3500kmLimit2")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        def firstTwoWinnersOfToursLessThan3500km = tdfWinners
                .grep { it.lengthKm < 3500 }
                .collect { it.name }
                .take(2)
        // firstTwoWinnersOfToursLessThan3500km - [Alberto Contador, Cadel Evans]
        println("firstTwoWinnersOfToursLessThan3500km - $firstTwoWinnersOfToursLessThan3500km")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        // filter by distinct - we want a new collection returned so pass mutate=false
        def distinctTDFWinners = tdfWinners.unique (false){ it.name }

        println("distinctTDFWinners - $distinctTDFWinners")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        long numberOfDistinceWinners = tdfWinners.unique (false){ it.name }?.size()
        // numberOfDistinceWinners - 8
        println("numberOfDistinceWinners - $numberOfDistinceWinners")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        // I think there's a 'bug' in the original article - the var's name would suggest to me (but I'm dyslexic so what do I know) 
        // that alternative names are pulled from collection but I think that var might
        // be incorrectly named - what the code does is actually skip the first 2 items
        // So, this version (involved I'll admit - easier if we could zip list like in Scala/Haskell)
        // does pull out alternative names
        def skipEveryOtherTDFWinner = (0..tdfWinners.size())
            .collectEntries{ [(it): tdfWinners[it]]}
            .grep { it.key.toInteger()%2 == 0}
            .collect { it.value}
        // skipEveryOtherTDFWinner - [Óscar Pereiro, Carlos Sastre, Andy Schleck, Bradley Wiggins, Vincenzo Nibali, Chris Froome]
        println("skipEveryOtherTDFWinner - $skipEveryOtherTDFWinner")
        
        // this is from the original DZone article - this will skip the first 2 from collection - not actually alternate
        skipEveryOtherTDFWinner = tdfWinners
                .stream()
                .skip(2)
                .collect(toList());
        // skipEveryOtherTDFWinner - [Carlos Sastre, Alberto Contador, Andy Schleck, Cadel Evans, Bradley Wiggins, Chris Froome, Vincenzo Nibali, Chris Froome, Chris Froome]
        System.out.println("skipEveryOtherTDFWinner #2 - " + skipEveryOtherTDFWinner);

        // here's that in Groovy
        skipEveryOtherTDFWinner = tdfWinners.drop(2)
        // skipEveryOtherTDFWinner - [Carlos Sastre, Alberto Contador, Andy Schleck, Cadel Evans, Bradley Wiggins, Chris Froome, Vincenzo Nibali, Chris Froome, Chris Froome]
        System.out.println("skipEveryOtherTDFWinner #3 - " + skipEveryOtherTDFWinner);

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'
        
        // I could use 'def' or the actual type - if the type is obvious I tend to leave it out
        List<String> mapWinnerYearNamesToList = tdfWinners.collect{ "${it.year} -  ${it.name}" }
        // mapWinnerYearNamesToList [2006 - Óscar Pereiro, 2007 - Alberto Contador, 2008 - Carlos Sastre, 2009 - Alberto Contador, 2010 - Andy Schleck, 2011 - Cadel Evans, 2012 - Bradley Wiggins, 2013 - Chris Froome, 2014 - Vincenzo Nibali, 2015 - Chris Froome, 2016 - Chris Froome]
        println("mapWinnerYearNamesToList $mapWinnerYearNamesToList")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        def mapWinnerNameLengthToList = tdfWinners.collect { it.name.length() }
        // mapWinnerNameLengthToList [13, 16, 13, 16, 12, 11, 15, 12, 15, 12, 12]
        println("mapWinnerNameLengthToList $mapWinnerNameLengthToList")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        // matching - allMatch, noneMatch
        Optional<Winner> winner2012 = tdfWinners.grep { it.name.contains('Wiggins')}
        // winner2012 - Bradley Wiggins
        println("winner2012 - ${winner2012.get()}")
        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        def winner2012_not_using_Optional = tdfWinners.grep { it.name.contains('Wiggins')}?.first()
        // winner2012 - Bradley Wiggins
        println("winner2012 - ${winner2012_not_using_Optional}")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        def winnerYear2014 = tdfWinners.grep {it.year == 2014}?.first()
        // winnerYear2014 - 2014
        println("winnerYear2014 - $winnerYear2014")

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'

        // reducing - 0 --> initial value
        int totalDistance = tdfWinners.sum {it.lengthKm}
        // totalDistance - 38767
        println("totalDistance - $totalDistance")

        def shortestYear = tdfWinners.min ( { a, b -> a.lengthKm <=> b.lengthKm } as Comparator )?.lengthKm
        // shortestYear - 3360
        println("shortestYear - $shortestYear")
        shortestYear = tdfWinners.collect { it.lengthKm }?.min()
        // shortestYear - 3360
        println("shortestYear - $shortestYear")

        def longestYear = tdfWinners.collect { it.lengthKm }?.max()
        // longestYear - 3661
        println("longestYear - $longestYear")

        def fastestWinner = tdfWinners.min { it.getAveSpeed() }
        println("fastestTDF - $fastestWinner")
        println("fastestTDF - ${fastestWinner.getAveSpeed()}")

          // groupingby - make a map whose keys are names
        def namesVsWinner = tdfWinners.groupBy { it.name }
        // namesVsWinner - {Bradley Wiggins=[Bradley Wiggins], Carlos Sastre=[Carlos Sastre], Cadel Evans=[Cadel Evans], Óscar Pereiro=[Óscar Pereiro], Chris Froome=[Chris Froome, Chris Froome, Chris Froome], Andy Schleck=[Andy Schleck], Alberto Contador=[Alberto Contador, Alberto Contador], Vincenzo Nibali=[Vincenzo Nibali]}
        println("namesVsWinner - $namesVsWinner")

        // join strings
        String allTDFWinnersTeamsCSV = tdfWinners.collect {it.team }.join(', ')
        // allTDFWinnersTeams Caisse d'Epargne–Illes Balears, Discovery Channel, Team CSC, Astana, Team Saxo Bank, BMC Racing Team, Team Sky, Team Sky, Astana, Team Sky, Team Sky
        println("allTDFWinnersTeams $allTDFWinnersTeamsCSV")
        // grouping
        Map<String, List<Winner>> winnersByNationality = tdfWinners.groupBy { it.nationality }
        // winnersByNationality - {Great Britain=[Bradley Wiggins, Chris Froome, Chris Froome, Chris Froome], Luxembourg=[Andy Schleck], Italy=[Vincenzo Nibali], Australia=[Cadel Evans], Spain=[Óscar Pereiro, Alberto Contador, Carlos Sastre, Alberto Contador]}
        println("winnersByNationality - $winnersByNationality")

        def winsByNationalityCounting = tdfWinners.groupBy { it.nationality }.collect { k, v -> [k, v.size()] }.flatten()
        // winsByNationalityCounting - {Great Britain=4, Luxembourg=1, Italy=1, Australia=1, Spain=4}
        println("winsByNationalityCounting - " + winsByNationalityCounting)

        assert tdfWinners.size() == 11, 'something has mutated the original collection...'
    }

    double getAveSpeed() {
        return (lengthKm / (winningTime.seconds / 3600) )
    }

    @Override
    String toString() {
        return name
    }
}

