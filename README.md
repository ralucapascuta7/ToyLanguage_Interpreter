# ToyLanguage Interpreter

## 📖 Description

ToyLanguage Interpreter is a complete interpreter for an educational programming language
that supports advanced programming concepts such as:
- Concurrent execution with fork/join
- Heap memory management with garbage collection
- Synchronization mechanisms (semaphores)
- Procedures with call by value
- Static type checking

The interpreter offers both a GUI (JavaFX) and CLI interface for step-by-step
program execution, allowing real-time visualization of all data structures.

---

## 🛠️ Technologies Used

### **Language & Framework**
- **Java 17** - Main programming language
- **JavaFX 21** - Framework for the graphical interface
- **Maven** - Build tool and dependency management

### **Concurrency & Synchronization**
- **ExecutorService** - Thread pool for concurrent execution
- **ReentrantLock** - Explicit synchronization for atomic operations
- **synchronized** - Implicit synchronization for shared structures

### **Design Patterns**
- **Interpreter Pattern** - For expression evaluation and statement execution
- **Composite Pattern** - For building the syntax tree (AST)
- **Repository Pattern** - For log persistence
- **MVC Pattern** - Separation of logic (Model), presentation (View), and control (Controller)
- **Factory Pattern** - For creating different types of values and statements

---

## ✨ Implemented Features

### **Core Language Features**

#### **1. Type System**
- **Primitive Types**: `int`, `bool`, `string`
- **Reference Types**: `Ref(T)` - pointers for heap allocation
- **Type Checker**: Static type validation before execution

#### **2. Statements**
- **Declarations**: `int v`, `Ref int a`
- **Assignments**: `v = expr`
- **Control Flow**: 
  - `if (cond) then stmt1 else stmt2`
  - `while (cond) stmt`
  - `for (v=exp1; v<exp2; v=exp3) stmt`
  - `switch (exp) (case exp1: stmt1) (case exp2: stmt2) (default: stmt3)`
- **I/O**: 
  - `print(expr)` - output
  - `openRFile(filename)`, `readFile(filename, var)`, `closeRFile(filename)`

#### **3. Memory Management**
- **Heap Allocation**: `new(var, expr)` - allocates memory on the heap
- **Heap Read**: `rH(expr)` - reads a value from the heap
- **Heap Write**: `wH(var, expr)` - writes a value to the heap
- **Garbage Collection**: 
  - Conservative GC - removes unreferenced heap cells
  - Runs automatically after each step

#### **4. Concurrency**
- **Fork Statement**: `fork(stmt)` - creates a new thread
  - Clones the SymTableStack
  - Shares Heap, Out, FileTable
- **ExecutorService**: Thread pool with 2 worker threads
- **Concurrent Execution**: oneStepForAllPrg() executes all threads simultaneously

#### **5. Synchronization Mechanisms**
##### **Count Semaphore**
```java
int sem;
createSemaphore(sem, 3);  // Semaphore with capacity 3
acquire(sem);              // Attempts to acquire the semaphore
// critical section
release(sem);              // Releases the semaphore
```
- **SemaphoreTable**: `Map<Integer, Pair<Integer, List<Integer>>>`
  - Pair: `(capacity, thread_list)`
- Allows N threads simultaneously (N = capacity)

---

## 🎨 GUI Features (JavaFX)

### **Window 1: Program Selector**
- ListView with all available programs
- Type checking before display
- Automatic filtering of invalid programs

### **Window 2: Main Execution Window**

---

## 📚 References & Concepts

### **Implemented from Course**
- **Lab 2-3**: Basic interpreter (expressions, statements)
- **Lab 4**: File operations (openRFile, readFile, closeRFile)
- **Lab 5-6**: Relational & logical expressions
- **Lab 7**: Heap management, garbage collection, while loops
- **Lab 8**: Concurrency with fork, thread management
- **Lab 10**: Type checker implementation
- **Lab 11**: JavaFX GUI implementation

### **Advanced Concepts**
- **Memory Management**: Heap allocation, garbage collection
- **Concurrency**: Fork/join, thread pools, ExecutorService
- **Synchronization**: Mutex locks, counting semaphores
- **Type Safety**: Static type checking before execution
- **Scope Management**: Stack of symbol tables for procedures

---

## 👨‍💻 Author

Project developed for the **Advanced Programming Methods (APM)** course  
Babeș-Bolyai University, Cluj-Napoca  
Academic year 2025-2026
