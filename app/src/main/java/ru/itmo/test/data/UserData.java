package ru.itmo.test.data;

import org.jetbrains.annotations.NotNull;

public class UserData implements Comparable {
    public int user_code;
    public int test_score;
    public long test_time;
    public String name;
    public String email;

    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof UserData){
            if (test_score - ((UserData) o).test_score == 0){
                if (test_time - ((UserData) o).test_time > 0) {
                    return -1;
                }else if(test_time - ((UserData) o).test_time < 0){
                    return 1;
                }else {
                    return 0;
                }
            }else{
                return test_score - ((UserData) o).test_score;
            }
        }else{
            return 0;
        }
    }
}
