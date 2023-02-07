% main
% set the values for the parameter 
main_rho = 28;
main_sigma = 10;
main_beta = 8/3;

% define the right handside of lorenze system as a function
%f = @(t,x) lorenz(t,x,sigma,rho,beta);
% Define a vector of equally spaced grid points.
k = 2;
h = 10^(-k);
tfinal = 1;
t = [0:h:tfinal];
%Set values for the initial data.
y0 = [-1;3;4];
% define the lorenze function
f = @(t,x) lorenz(t,x,main_sigma,main_rho,main_beta);
% ask the user for the method
option = input('Please choose one method below\n1.Euler’s method\n2.explicit Runge–Kutta\n3.implicit Runge–Kutta\noption:');

% define tout and yout out of the scope of if structes 
tout = -1;
yout = -1;
name = "";

if option == 1
    % if option == 1, call Euler’s method
    [tout,yout] = EulerSolver(f,t,y0);
    name = "Euler";
elseif option == 2
    % if option == 2, call explicit Runge–Kutta method
    [tout,yout] = RK4Solver(f,t,y0);
    name = "RK4";
elseif option == 3
    % if option == 2, call implicit Runge–Kutta method
    [tout,yout] = IRK4Solver(f,t,y0);
    name = "IRK4";
else 
    fprintf("Prompt: please enter number 1,2 or 3\n");
end;


% store the result into a Y
Y = yout;

% solve the same equation using ode45
options = odeset('RelTol',3.1e-14,'AbsTol',1e-16);
[t,Ym] = ode45(f,t,y0,options);

Ym = Ym';
%calculate the erros
main_error = max(max(abs(Y-Ym)))

%{
% to do plot the graph
% use meshgrid to plot X and Y
figure('name',name);
plot3(Y(1,:),Y(2,:),Y(3,:));
shading interp;

% do the same thing to plot figure 2
figure('name','ODE45');
plot3(Ym(1,:),Ym(2,:),Ym(3,:));
shading interp;
%}

