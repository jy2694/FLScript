# FLScript

### DEVELOPING

![version](https://img.shields.io/badge/FLScript-NONE-blue)
![MCV](https://img.shields.io/badge/Spigot-+1.19-ED8106?logo=spigotmc)

##### FLScript allows you to customize the functionality of events that occur to you in Minecraft through the scripting language. 

##### It is a unique scripting language that is more specialized in creating features and is constantly updated.

---

### Install

1. Insert the FLScript plug-in .jar file into the plugins folder on the server.


2. Running the server once initially creates a folder called FLScript under the plugins folder and is ready to write the script.

---

### Support Event

| EventType           | Description                                | Variable         |
|---------------------|--------------------------------------------|------------------|
| PlayerMove          | When x, y, and z of the player are changed | player, from, to |
| PlayerBreakBlock    | When the player breaks the block           | player, block    |
| PlayerPlaceBlock    | When the player placed the block           | player, block    |
| PlayerInteractBlock | When a player interacts with a block       | player, block    |
| PlayerHitEntity     | When a player hits an entity               | player, entity   |
| PlayerDamage        | When the player is damaged                 | player, entity   |

##### ***Event types will be added later.***

---

### File Structure

| folder                       | scriptType          |
|------------------------------|---------------------|
| FLScript/PlayerMove          | PlayerMove          |
| FLScript/PlayerBreakBlock    | PlayerBreakBlock    |
| FLScript/PlayerPlaceBlock    | PlayerPlaceBlock    |
| FLScript/PlayerInteractBlock | PlayerInteractBlock |
| FLScript/PlayerHitEntity     | PlayerHitEntity     |
| FLScript/PlayerDamage        | PlayerDamage        |

##### ***You can create script files with the [.flscript] extension in event folder.***

---

### Syntax

* Variable
  * Variables are declared at the same time as assignments are made.
  ```
  x = 10
  ```
  * Typically, a variable is a local variable that is only available within a script file.
  * However, if you put '$' before the variable, it is a global variable shared by all script files.
  ```
  x = 10
  $x = 30
  println('x =' + x)
  println('$x =' + $x)
  
  //Result
  //x = 10
  //$x = 30
  ```
* Type
  * Users can create and use three values: String, Number, and Bool.
  ```
  //below is string
  a = 'this is string'
  b = "this is string"
  
  //below is number
  c = 10
  d = 10.5
  
  //below is bool
  e = true
  f = false
  ```
  * Three types are handled using literal.
  * There are reference types that you cannot create. This type is the default value assigned to the event.
* Operator
  * Arithmetic operator
  ```
    +           //plus    ; Add two values. If the string is an operand, join the string.
    -           //minus   ; Subtract the two values.
    *           //multiply; Multiply the two values.
    /           //divide  ; Divide the two values.
    //          //quotient; Divide the two values to obtain the quotient.
    %           //mod     ; Divide the two values to obtain the remainder.
    **          //pow     ; Get the power of the first value and the second value.
  ```
  * Logical operator 
  ```
    &           //and     ; Conjunction operator, And operator.
    |           //or      ; Disjunction operator, Or operator.
    ^           //xor     ; Exclusive-Or operator.
    !           //not     ; Outputs the logical value in reverse.
  ```
  * Comparison operator
  ```
    ==          //equals  ; Returns true if the two values are equal. It also checks the type.
    >           //greater ; Returns true if the first value is larger.
    <           //less    ; Returns true if the first value is smaller.
  ```
  * Assignment operator 
  ```
    =           //assign  ; Assign a second value to the variable that is the first operand.
  ```
  * Reference operator
  ```
    .           //dot     ; Access properties of the reference type.
  ```
* Control Statement
  * If
    * If the condition is true, execute the code block of braces.
      ```
        a = 10
        b = 10
        if(a == b){
            println('true!')
        }
      ```
  * While
    * If the condition is true, repeat the code block of braces.
      ```
      a = 10
      b = 1
      while(b < a){
        println(b);
        b = b + 1
      }
      ```
* Built-in function
  * print(type:any)
    * Outputs Argument
  * println(type:any)
    * Outputs Argument and \n at the end
  * sin(type:number)
    * If the radian value is received as an argument, it returns the corresponding sin value.
  * cos(type:number)
    * If the radian value is received as an argument, it returns the corresponding cos value.
  * tan(type:number)
    * If the radian value is received as an argument, it returns the corresponding tan value.
  * radian(type:number)
    * If the degree value is received as an argument, it returns the corresponding radian value.
  * degree(type:number)
    * If the radian value is received as an argument, it returns the corresponding degree value.
  * string(type:any)
    * Cast the value entered as an argument as a string type.
  * number(type:any)
    * Cast the value entered as an argument as a number type.
  * bool(type:number)
    * Cast the value entered as an argument as a bool type.