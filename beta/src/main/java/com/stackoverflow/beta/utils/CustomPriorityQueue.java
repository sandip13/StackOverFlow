package com.stackoverflow.beta.utils;


import com.stackoverflow.beta.model.Question;

import java.util.*;

final public class CustomPriorityQueue extends PriorityQueue<Question> {

    private static CustomPriorityQueue customPriorityQueue;


    private CustomPriorityQueue() {
    }
    private CustomPriorityQueue(Comparator comparator){
        super(comparator);
    }

    public static CustomPriorityQueue getCustomPriorityQueue() {

        if (customPriorityQueue == null) {
            synchronized (CustomPriorityQueue.class) {
                if (customPriorityQueue == null) {
                    customPriorityQueue = new CustomPriorityQueue((o1, o2) -> ((Question)o1).getVotes()-((Question)o2).getVotes());
                }
            }
        }
        return customPriorityQueue;

    }
    final int MAX_SIZE = 100;

    @Override
    public boolean add(Question question) {
        boolean result = super.add(question);
        while (size() > MAX_SIZE) {
            poll();
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends Question> c) {
        boolean result = false;
        for (Question e : c) {
            result |= add(e);
        }
        return result;
    }

    public boolean containsKey(Question question) {
        for (Question question1 : customPriorityQueue) {
            if (question1.getId() == question.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean updateKey(Question question){
        if(!containsKey(question)){
            return false;
        }
        List<Question> questionsQueue=new LinkedList<>();
        while(!customPriorityQueue.isEmpty()){
            Question question1 = customPriorityQueue.poll();
            if(question1.getId() != question.getId()) {
                questionsQueue.add(question1);
            }
        }
        questionsQueue.add(question);
        customPriorityQueue.addAll(questionsQueue);
        return true;
    }
}
