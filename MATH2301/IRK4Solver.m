function [tout,yout] = IRK4Solver(f,t,y0) 
    t = t(:); % ensure that t is a column vector
    N = length(t); 
    h = t(2)-t(1); % calculate h by assuming t gridpoints are equidistant
    y0 = y0(:); % ensures that y0 is a column vector
    y = zeros(3,N);% allocate the output array y
    y(:,1) = y0; % assign y0 to the first column of y

    tout = t;
    yout = y;
    
    %calculate the loop
    %Iterative 
    for k=1:N - 1 
        F=@(xi)[yout(:,k)+h*(1/4*f(t(k)+h*(1/2-sqrt(3)/6),xi(1:3))...
              +(1/4-sqrt(3)/6)*f(t(k)+(1/2+sqrt(3)/6)*h,xi(4:6)))-xi(1:3);
              yout(:,k)+h*((1/4+sqrt(3)/6)*f(t(k)+h*(1/2-sqrt(3)/6),xi(1:3))...
              +1/4*f(t(k)+(1/2+sqrt(3)/6)*h,xi(4:6)))-xi(4:6)];
          u0=[0;0;0]; % initial guess for xi(1:3) vector
          v0=[0;0;0]; % initial guess for xi(4:6) vector
          options=optimoptions(@fsolve,'FunctionTolerance',3.1e-14,'OptimalityTolerance',1e-16);
          xistar=fsolve(F,[u0;v0],options);
          yout(:,k+1)=yout(:,k)+h*(1/2*f(t(k)+h*(1/2-sqrt(3)/6),xistar(1:3))...
                   + 1/2*f(t(k)+h*(1/2+sqrt(3)/6),xistar(4:6)));
    end
    
