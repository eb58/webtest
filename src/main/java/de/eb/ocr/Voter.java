package de.eb.ocr;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class Voter implements Comparator {

    private class SearchResultWrapper {

        public double d;
        public SearchResult sr;

        public SearchResultWrapper(double d, SearchResult sr) {
            this.d = d;
            this.sr = sr;
        }
    }
    private TreeMap<Character, SearchResultWrapper> tm1 = new TreeMap<Character, SearchResultWrapper>();    // tm1: natürliche Sortierung ohne
    private TreeMap<Character, SearchResultWrapper> tm2 = new TreeMap<Character, SearchResultWrapper>(this);

    @Override
    public int compare(Object o1, Object o2) {
        SearchResultWrapper dd1 = tm1.get((Character) o1);
        SearchResultWrapper dd2 = tm1.get((Character) o2);
        return (dd1.d < dd2.d) ? 1 : (dd1.d > dd2.d) ? -1 : 0;
    }

    public void add(SearchResult sr) {
        if (sr == null)
            return;
        double mq = sr.getMQ();

        if (tm1.containsKey(sr.getBestChar())) {
            double d = tm1.get(sr.getBestChar()).d;
            tm1.put(sr.getBestChar(), new SearchResultWrapper(d * mq, sr));
        } else
            tm1.put(sr.getBestChar(), new SearchResultWrapper(mq, sr));
    }

    public void doSort() {
        tm2.putAll(tm1);
    }

    public SearchResult getBest() {
        return tm2.entrySet().iterator().next().getValue().sr;
    }

    public SearchResult getSecondBest() {
        if (tm2.size() <= 1)
            return null;

        Iterator it = tm2.entrySet().iterator();
        it.next();
        if (it.hasNext()) {
            Map.Entry<SearchResult, Double> me = (Map.Entry<SearchResult, Double>) it.next();
            return me.getKey();
        }
        return null;
    }

    public SearchResult vote(boolean sim) {
        this.doSort();

        if (tm2.size() == 1) {
            getBest().setConf(sim ? 1.0 : 0.9);
            return getBest();
        }

        Iterator it = tm2.entrySet().iterator();
        Map.Entry me = (Map.Entry) it.next();
        SearchResultWrapper srw1 = (SearchResultWrapper) me.getValue();

        me = (Map.Entry) it.next();
        SearchResultWrapper srw2 = (SearchResultWrapper) me.getValue();
        double d1 = srw1.d;
        double d2 = srw2.d;
        double mq = d1 / d2;

        getBest().setConf(1.0 - 1.0 / mq);
        return getBest();
    }
}
