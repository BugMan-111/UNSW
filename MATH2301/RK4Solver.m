function [tout, yout] = RK4Solver(f, t, y0)
%tfinal = 5; need to be defined.
N = size(t);
N = N(2);
tout = t(:);
yout = zeros(3,N);
yout(:,1)= y0;
h = t(2) - t(1);
for i = 1 : N - 1 
    %h = t(i) - t(i-1);or to be defined
    k1 = f(t(i), yout(:,i));
    k2 = f(t(i)+ 0.5*h,  yout(:,i)+ 0.5*k1*h);
    k3 = f(t(i)+ 0.5*h,  yout(:,i)+ 0.5*k2*h);
    k4 = f(t(i) + h,  yout(:,i)+ k3*h);
    yout(:,i+1) = yout(:,i) + 1/6 *(k1+ 2*k2 +2*k3+k4)*h;
end


