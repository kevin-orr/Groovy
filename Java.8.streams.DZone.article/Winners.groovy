import java.time.Duration

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

    int year
    String nationality
    String name
    String team
    int lengthKm
    Duration winningTime
    int stageWins
    int daysInYellow

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

//        Runnable r = () -> {
//            System.out.println("Hi im " + finalString)
//            System.out.println("Hi im " + effectivelyFinalString)
//        }
//        new Thread(r).start()

        // Filter and Map -
        def winnersOfToursLessThan3500km = tdfWinners
                .grep { it.lengthKm < 3500 }
                .collect { it.name }
        // Winners of Tours Less than 3500km - [Alberto Contador, Cadel Evans, Bradley Wiggins, Chris Froome, Chris Froome]
        println("Winners of Tours Less than 3500km - $winnersOfToursLessThan3500km")

        def winnersOfToursGreaterThan3500km = tdfWinners
                .grep { it.lengthKm >= 3500 }
                .collect {it.name}
        // Winners of Tours Greater than 3500km - [Óscar Pereiro, Alberto Contador, Carlos Sastre, Andy Schleck, Vincenzo Nibali, Chris Froome]
        println("Winners of Tours Greater than 3500km - $winnersOfToursGreaterThan3500km")

        // limit -
        def winnerObjectsOfToursLessThan3500kmLimit2 = tdfWinners
                .grep { it.lengthKm < 3500 }
                .collect { it.name }
                .take(2)

        // winnerObjectsOfToursLessThan3500kmLimit2 [Alberto Contador, Cadel Evans]
        println("winnerObjectsOfToursLessThan3500kmLimit2 $winnerObjectsOfToursLessThan3500kmLimit2")

        def firstTwoWinnersOfToursLessThan3500km = tdfWinners
                .grep { it.lengthKm < 3500 }
                .collect { it.name }
                .take(2)

        // firstTwoWinnersOfToursLessThan3500km - [Alberto Contador, Cadel Evans]
        println("firstTwoWinnersOfToursLessThan3500km - $firstTwoWinnersOfToursLessThan3500km")
        // filter by distinct
        def distinctTDFWinners = tdfWinners.unique{ it.name }

        println("distinctTDFWinners - $distinctTDFWinners")
        long numberOfDistinceWinners = tdfWinners.unique{ it.name }.size()
        // numberOfDistinceWinners - 8
        println("numberOfDistinceWinners - $numberOfDistinceWinners")
//        // skip records
//        List<Winner> skipEveryOtherTDFWinner = tdfWinners
//                .stream()
//                .skip(2)
//                .collect(toList())
//        // skipEveryOtherTDFWinner - [Carlos Sastre, Alberto Contador, Andy Schleck, Cadel Evans, Bradley Wiggins, Chris Froome, Vincenzo Nibali, Chris Froome, Chris Froome]
//        System.out.println("skipEveryOtherTDFWinner - " + skipEveryOtherTDFWinner)
        List<String> mapWinnerYearNamesToList = tdfWinners.collect{ "${it.year} -  ${it.name}" }
        // mapWinnerYearNamesToList [2006 - Óscar Pereiro, 2007 - Alberto Contador, 2008 - Carlos Sastre, 2009 - Alberto Contador, 2010 - Andy Schleck, 2011 - Cadel Evans, 2012 - Bradley Wiggins, 2013 - Chris Froome, 2014 - Vincenzo Nibali, 2015 - Chris Froome, 2016 - Chris Froome]
        println("mapWinnerYearNamesToList $mapWinnerYearNamesToList")


//        List<Integer> mapWinnerNameLengthToList = tdfWinners
//                .stream()
//                .map(Winner::getName)
//                .map(String::length)
//                .collect(toList())
//        // mapWinnerNameLengthToList [13, 16, 13, 16, 12, 11, 15, 12, 15, 12, 12]
//        System.out.println("mapWinnerNameLengthToList " + mapWinnerNameLengthToList)
//        // matching - allMatch, noneMatch
//        Optional<Winner> winner2012 = tdfWinners.stream().filter(w -> w.getName().contains("Wiggins")).findAny()
//        // winner2012 - Bradley Wiggins
//        System.out.println("winner2012 - " + winner2012.get())
//        Optional<Integer> winnerYear2014 = tdfWinners.stream().map(Winner::getYear).filter(x -> x == 2014).findFirst()
//        // winnerYear2014 - 2014
//        System.out.println("winnerYear2014 - " + winnerYear2014.get())
//        // reducing - 0 --> initial value
//        int totalDistance = tdfWinners.stream().map(Winner::getLengthKm).reduce(0, Integer::sum)
//        // totalDistance - 38767
//        System.out.println("totalDistance - " + totalDistance)
//        Optional<Integer> shortestYear = tdfWinners.stream().map(Winner::getLengthKm).reduce(Integer::min)
//        // shortestYear - 3360
//        System.out.println("shortestYear - " + shortestYear.get())
//        Optional<Integer> longestYear = tdfWinners.stream().map(Winner::getLengthKm).reduce(Integer::max)
//        // longestYear - 3661
//        System.out.println("longestYear - " + longestYear.get())
//        Optional<Winner> fastestWinner = tdfWinners.stream().min(Comparator.comparingDouble(Winner::getAveSpeed))
//        System.out.println("fastestTDF - " + fastestWinner.get())
//        // shorthand
//        OptionalDouble fastestTDF = tdfWinners.stream().mapToDouble(Winner::getAveSpeed).min()
//        // fastestTDF - 39.0
//        System.out.println("fastestTDF - " + fastestTDF.getAsDouble())
//        // groupingby - make a map whose keys are names
//        Map<String, List<Winner>> namesVsWinner = tdfWinners.stream().collect(groupingBy(Winner::getName))
//        // namesVsWinner - {Bradley Wiggins=[Bradley Wiggins], Carlos Sastre=[Carlos Sastre], Cadel Evans=[Cadel Evans], Óscar Pereiro=[Óscar Pereiro], Chris Froome=[Chris Froome, Chris Froome, Chris Froome], Andy Schleck=[Andy Schleck], Alberto Contador=[Alberto Contador, Alberto Contador], Vincenzo Nibali=[Vincenzo Nibali]}
//        System.out.println("namesVsWinner - " + namesVsWinner)
//        // join strings
//        String allTDFWinnersTeamsCSV = tdfWinners.stream().map(Winner::getTeam).collect(joining(", "))
//        // allTDFWinnersTeams Caisse d'Epargne–Illes Balears, Discovery Channel, Team CSC, Astana, Team Saxo Bank, BMC Racing Team, Team Sky, Team Sky, Astana, Team Sky, Team Sky
//        System.out.println("allTDFWinnersTeams " + allTDFWinnersTeamsCSV)
//        // grouping
//        Map<String, List<Winner>> winnersByNationality = tdfWinners.stream().collect(groupingBy(Winner::getNationality))
//        // winnersByNationality - {Great Britain=[Bradley Wiggins, Chris Froome, Chris Froome, Chris Froome], Luxembourg=[Andy Schleck], Italy=[Vincenzo Nibali], Australia=[Cadel Evans], Spain=[Óscar Pereiro, Alberto Contador, Carlos Sastre, Alberto Contador]}
//        System.out.println("winnersByNationality - " + winnersByNationality)
//        Map<String, Long> winsByNationalityCounting = tdfWinners.stream().collect(groupingBy(Winner::getNationality, counting()))
//        // winsByNationalityCounting - {Great Britain=4, Luxembourg=1, Italy=1, Australia=1, Spain=4}
//        System.out.println("winsByNationalityCounting - " + winsByNationalityCounting)
    }

    public double getAveSpeed() {
        return (getLengthKm() / (getWinningTime().getSeconds() / 3600) )
    }

    @Override
    public String toString() {
        return name
    }
}

