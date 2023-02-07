function [tout,yout] = EulerSolver(f,t,y0)
    N = length(t);
    y = zeros(3,N); % allocate the output array y
    y(:,1) = y0; % assign y0 to the first column of y
    h = t(2) - t(1);
    for n = 1:(N - 1)
         y(:,n+1) = y(:,n) + h*f(t(n),y(:,n)); % defines y_{n+1} from y_n
    end
    tout = t(:);
    yout = y;

