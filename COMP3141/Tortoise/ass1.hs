import Test.QuickCheck
import Data.Char
import Data.List

-- | The actual Tortoise Graphics Language.
data Instructions
  = Move Int Instructions
    -- ^ Move distance in the current facing direction. Will draw a line
    --   if the pen is down.
  | Turn Int Instructions
    -- ^ Rotate facing direction by Angle.
  | SetStyle Int Instructions
    -- ^ Change the current line style.
  | SetColour Int Instructions
    -- ^ Change the current line colour.
  | PenDown Instructions
    -- ^ Put the pen down, so that subsequent Move instructions will draw.
  | PenUp Instructions
    -- ^ Lift the pen up , so that subsequent Move instructions will not draw.
  | Stop
    -- ^ Termination
  deriving (Show,Eq)


a = Move 100 Stop
b = Move 200 a
c = SetColour 3 b
d = Move 300 c
e = Move 400 d
f = SetColour 2 e
g = Move 500 f
h = Move 600 g
i = SetColour 1 h


andThen :: Instructions -> Instructions -> Instructions
andThen i1 i2 = myconcat i2 i1 
 where
myconcat :: Instructions -> Instructions -> Instructions
myconcat new Stop = new
myconcat new (Turn int i) = Turn int (myconcat new i)
myconcat new (Move int i) = Move int (myconcat new i)
myconcat new (SetStyle int i) = SetStyle int (myconcat new i)
myconcat new (SetColour int i) =SetColour int (myconcat new i)
myconcat new (PenDown i)  = PenDown (myconcat new i)
myconcat new (PenUp i)  = PenUp (myconcat new i)


toList :: Instructions -> [Instructions]
toList (Move int i) = (Move int Stop) : toList i  
toList (Turn int i) = (Turn int Stop): toList i  
toList (SetStyle int (SetStyle int1 i)) = toList (SetStyle int1 i)
toList (SetStyle int i) = (SetStyle int Stop): toList i
toList (SetColour int (SetColour int1 i)) = toList (SetColour int1 i) 
toList (SetColour int i) = (SetColour int Stop): toList i  
toList (PenDown (PenDown i)) = toList (PenDown i)  
toList (PenDown (PenUp i)) = toList (PenUp i) 
toList (PenDown i) = (PenDown Stop): toList i  
toList (PenUp (PenDown i)) = toList (PenDown i)  
toList (PenUp (PenUp i)) = toList (PenUp i) 
toList (PenUp i) = (PenUp Stop): toList i   
toList Stop = []

toListCoulour :: Instructions -> [Instructions]
toListCoulour (Move int i) = toListCoulour i  
toListCoulour (Turn int i) = toListCoulour i  
toListCoulour (SetStyle int i) =  toListCoulour i  
toListCoulour (SetColour int (SetColour int1 i)) = toListCoulour (SetColour int1 i) 
toListCoulour (SetColour int i) = (SetColour int Stop): toListCoulour i  
toListCoulour (PenDown i) =  toListCoulour i  
toListCoulour (PenUp i)  =  toListCoulour i  
toListCoulour Stop = []

toListSytle :: Instructions -> [Instructions]
toListSytle (Move int i) = toListSytle i  
toListSytle (Turn int i) = toListSytle i  
toListSytle (SetStyle int (SetStyle int1 i)) = toListSytle (SetStyle int1 i) 
toListSytle (SetStyle int i) = (SetStyle int Stop):toListSytle i  
toListSytle (SetColour int i) = toListSytle i  
toListSytle (PenDown i) =  toListSytle i  
toListSytle (PenUp i)  =  toListSytle i  
toListSytle Stop = []

toListPen :: Instructions -> [Instructions]
toListPen (Move int i) = toListPen i  
toListPen (Turn int i) = toListPen i  
toListPen (SetStyle int i) = toListPen i 
toListPen (SetColour int i) = toListPen i  
toListPen (PenDown (PenDown i)) = toListPen (PenDown i)  
toListPen (PenDown (PenUp i)) = toListPen (PenUp i) 
toListPen (PenDown i) = (PenDown Stop): toListPen i  
toListPen (PenUp (PenDown i)) = toListPen (PenDown i)  
toListPen (PenUp (PenUp i)) = toListPen (PenUp i) 
toListPen (PenUp i) = (PenUp Stop): toListPen i  
toListPen Stop = []

reverse1 :: [a] -> [a]
reverse1 l = go l []
 where go [] a = a
       go (x:xs) a = go xs (x:a)

helper2 :: [Instructions] -> [Instructions] -> Instructions
helper2 [] [] = Stop
helper2 xs [] = head xs
helper2 [] xs = head xs
helper2 xs1 xs2 = andThen (head xs1) (head xs2)

helper1 :: [Instructions] -> [Instructions] -> [Instructions] -> Instructions
helper1 ((Move int i) : xs) xs1 xs2 = andThen (Move (-1 * int) i) (helper1 xs xs1 xs2)
helper1 ((Turn int i) : xs) xs1 xs2 = andThen (Turn (-1 * int) i) (helper1 xs xs1 xs2)
helper1 ((PenDown i) : xs) xs1 xs2 = andThen (PenUp i) (helper1 xs xs1 xs2)
helper1 ((PenUp i) : xs) xs1 xs2 = andThen (PenDown i) (helper1 xs xs1 xs2)
helper1 ((SetColour int i) : xs) ((SetColour int1 i1) : xs1) xs2 = andThen (SetColour int1 i1) (helper1 xs xs1 xs2)
helper1 ((SetStyle int i) : xs) xs1 ((SetStyle int1 i1) : xs2) = andThen (SetStyle int1 i1) (helper1 xs xs1 xs2)
helper1 (Stop : xs) xs1 xs2 = Stop
helper1 [] xs1 xs2 = Stop

helper0 :: Instructions -> [Instructions] -> Instructions
helper0 (Move int i) xs = Move int (helper0 i xs) 
helper0 (Turn int i) xs = Turn int (helper0 i xs)
helper0 (SetColour int i) xs = SetColour int (helper0 i xs)
helper0 (SetStyle int i) xs = SetStyle int (helper0 i xs)
helper0 (PenDown i) ((PenDown i1):xs) = PenDown (helper0 i xs)
helper0 (PenDown i) ((PenUp i1):xs) = PenUp (helper0 i xs)
helper0 (PenUp i) ((PenDown i1):xs) = PenDown (helper0 i xs)
helper0 (PenUp i) ((PenUp i1):xs) = PenUp (helper0 i xs)
helper0 Stop xs = Stop

retrace :: Instructions -> Instructions
retrace i = helper0 a (reverse1 (toListPen i)) 
 where
 a = andThen (helper2 (reverse1 (toListCoulour i)) (reverse1 (toListSytle i)))  (helper1 (reverse1 (toList i)) (tail(reverse1 (toListCoulour i)) ++ [SetColour 12000 Stop]) (tail(reverse1 (toListSytle i)) ++ [SetStyle 13000 Stop]))




