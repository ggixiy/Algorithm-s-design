using System;

namespace ExternalSorting
{
    class MainClass
    {
        public static void Main(string[] args)
        {
            MemoryUsage m = new MemoryUsage();
            TestMemory t = new TestMemory();

            m.CheckMemory();
            t.Func();

            for (int i = 0; i < 10; i++)
            {
                Console.WriteLine("Hello world " + i);
            }

            m.CheckMemory();
        }
    }
}