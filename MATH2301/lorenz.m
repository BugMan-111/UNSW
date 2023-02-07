function y = lorenz(t,x,sigma,rho,beta)

    % time should always be postive, and real 
    if (t < 0) || (isreal(t) == false) 
        error('t should be inputted as a real value which indicates time.')
    end
    
    % create a 3 * 1 matrix for 3 equations
    y = zeros(3,1);
    % σ(y − x)
    y(1) = sigma * (x(2) - x(1));
    % x(ρ − z) − y
    y(2) = x(1) * (rho - x(3)) - x(2);
    % xy − βz
    y(3) = x(1) * x(2) - beta * x(3);    
end
