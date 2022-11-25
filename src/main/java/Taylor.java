public class Taylor {
    public static double sin(double x, int n){
        double sum = 0;
        byte sign = 1;
        for(int i = 1 ; i < n*2 ; i++){
            if(i % 2 != 0 ){
                double v = Math.pow(x,i);
                long fac = factorial(i);
                sum += sign* (v/fac);
                sign *=-1;
            }
        }
        return sum;
    }
    public static long factorial(long x){
        if( x <= 2)
            return x;
        else
            return x*factorial(x-1);
    }
}
