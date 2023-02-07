module TortoiseCombinators
       ( andThen 
       , loop 
       , invisibly 
       , retrace 
       , overlay 
       ) where

import Tortoise

-- See Tests.hs or the assignment spec for specifications for each
-- of these combinators.

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

loop :: Int -> Instructions -> Instructions
loop n i 
 | n < 0 = Stop
 | otherwise = handleloop n Stop i
 where
handleloop :: Int -> Instructions -> Instructions -> Instructions
handleloop 0 accum i = andThen accum Stop
handleloop n accum i = handleloop (n - 1) (andThen accum i) i

invisibly :: Instructions -> Instructions
invisibly i = helper (andThen (PenUp Stop) i) (PenDown Stop) 1
 where
helper :: Instructions -> Instructions -> Int -> Instructions
helper (Move int i) prev_pen_state n = Move int (helper i prev_pen_state (n + 1))
helper (Turn int i) prev_pen_state n = Turn int (helper i prev_pen_state (n + 1))
helper (SetStyle int i) prev_pen_state n = SetStyle int (helper i prev_pen_state (n + 1))
helper (SetColour int i) prev_pen_state n = SetColour int (helper i prev_pen_state (n + 1))
helper (PenDown i) prev_pen_state n = (helper i (PenDown Stop) (n + 1))
helper (PenUp i) prev_pen_state n 
 | n == 1 = PenUp (helper i (PenDown Stop) (n + 1))
 | otherwise = PenUp (helper i (PenUp Stop) (n + 1))
helper Stop prev_pen_state n = andThen prev_pen_state Stop

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
retrace i = helper0 a (tail(reverse1 (toListPen i) ++ [(PenDown Stop)]))
 where
 a = andThen (helper2 (reverse1 (toListCoulour i)) (reverse1 (toListSytle i)))  (helper1 (reverse1 (toList i)) (tail(reverse1 (toListCoulour i)) ++ [SetColour white Stop]) (tail(reverse1 (toListSytle i)) ++ [SetStyle (Solid 1) Stop]))


overlay :: [Instructions] -> Instructions
overlay [] = Stop
overlay (i:is) = andThen (andThen i (invisibly (retrace i))) (overlay is)


