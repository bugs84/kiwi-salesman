package cz.vondr.kiwi.miky;

import cz.vondr.kiwi.data.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MikyChroupacDat {
    private Data data;

    public MikyChroupacDat(Data data) {

        this.data = data;
    }

    public void chroupejData() {

        generateFragments(data.destsArrivals, data.destsDepartures, data.numberOfCities);
        //TADY TED MUZES PSAT KOD NA CHROUPANI DAT
    }

    private void generateFragments(
            List<Destination> destsArrivals,
            List<Destination> destsDepartures,
            int numberOfCities) {

//        var  dayCheapestFragments,
//                fragmentsPriceDestsDays,// = {},
//                fragmentsDestTotallyBest,// = {},
//                destMinimums,// = {},
//
//                price,
//                j,
//                dayCheapestFragment;

        int daysTotal = numberOfCities - 1;

        // from second (endpoint can't have fragment)
        for (short destination = 1; destination < numberOfCities; destination++) {

            // try each day
            for (short day = 0; day < daysTotal; day++) {
                List<Flight> arrivals = destsArrivals.get(destination).days.get(day).flights;
                List<Flight> departures = destsDepartures.get(destination).days.get(day).flights;

                // both arrays exists
                if (!arrivals.isEmpty() && !departures.isEmpty()) {
                    // day and destination already decided
                    List<Fragment> dayCheapestFragments = getDayCheapestFragmentsFor(arrivals, departures);

                    // some fragments found
                    if (!dayCheapestFragments.isEmpty()) {
                        int price = dayCheapestFragments.get(0).price;

                        FragmentsDestination fragmentsDestination = getFragmentsDestination(data.fragmentsDestsDays, destination);
                        FragmentsDay fragmentsDay = getFragmentsDay(fragmentsDestination.days, day);
                        fragmentsDay.dayBestPrice = price;

                        if (fragmentsDestination.minimum == -1) {
                            fragmentsDestination.minimum = price;
                        } else if (price < fragmentsDestination.minimum) {
                            fragmentsDestination.minimum = price;
                        }
                    }
                }
            }
        }

//        return {
//                fragmentsPriceDestsDays:fragmentsPriceDestsDays,
//                fragmentsDestTotallyBest:fragmentsDestTotallyBest
//        };
    }

    private void addFragment(List<Fragment> fragments, Flight arrival, Flight departure, int price) {
        fragments.add(new Fragment(arrival, departure, price));
    }


    private List<Fragment> getDayCheapestFragmentsFor(List<Flight> arrivals, List<Flight> departures) {
        List<Fragment> fragments = new ArrayList<>();
        int cheapestArrDep = -1;
        int cheapestDepArr = -1;
        HashSet<String> alreadyAddedInFirstLoop = new HashSet<>();

        // first loop A -> A, A -> B, A-> C
        arrivalDepartureLoop:
        for (Flight arrival : arrivals) {
            for (Flight departure : departures) {
                // cycle A -> A
                if (arrival.from == departure.destination) {
                    continue;
                }

                // actual price for this exact fragment
                int price = arrival.price + departure.price;

                String id = arrival.from + "rel" + arrival.destination;

                // first min
                if (cheapestArrDep == -1) {
                    cheapestArrDep = price;
                    alreadyAddedInFirstLoop.add(id);
                    addFragment(fragments, arrival, departure, price);
                } else if (price == cheapestArrDep) {
                    // next exactly same min
                    alreadyAddedInFirstLoop.add(id);
                    addFragment(fragments, arrival, departure, price);
                } else {
                    // growing price - no need to continue, I want only very first one
                    break arrivalDepartureLoop;
                }
            }
        }

        // second loop A -> A, B -> A, C -> A
        departureArrivalLoop:
        for (Flight departure : departures) {
            // arrivalLoop:
            for (Flight arrival : arrivals) {
                // cycle A -> A
                if (arrival.from == departure.destination) {
                    continue;
                }

                int price = arrival.price + departure.price;

                // already have better from first loop
                // todo Miky check which loop?
                if (price > cheapestArrDep) {
                    break; // arrivalLoop
                    //break departureArrivalLoop;
                }

                String id = arrival.from + "rel" + arrival.destination;

                // first min
                if (cheapestDepArr == -1) {
                    cheapestDepArr = price;

                    // delete all results from first loop
                    if (cheapestDepArr < cheapestArrDep) {
                        fragments.clear();
                        alreadyAddedInFirstLoop.clear();
                    }
                    // check by id
                    if (!alreadyAddedInFirstLoop.contains(id)) {
                        addFragment(fragments, arrival, departure, price);
                    }
                } else if (price == cheapestDepArr) {
                    // next exactly same min
                    // check by id
                    if (!alreadyAddedInFirstLoop.contains(id)) {
                        addFragment(fragments, arrival, departure, price);
                    }
                } else {
                    // growing price
                    break departureArrivalLoop; // eslint-disable-line no-labels
                }
            }
        }

        return fragments;
    }

    private FragmentsDestination getFragmentsDestination(List<FragmentsDestination> list, short index) {
        while (list.size() <= index) {
            list.add(new FragmentsDestination());
        }
        return list.get(index);
    }

    private FragmentsDay getFragmentsDay(List<FragmentsDay> list, short index) {
        while (list.size() <= index) {
            list.add(new FragmentsDay());
        }
        return list.get(index);
    }
}
