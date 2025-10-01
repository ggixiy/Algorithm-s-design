using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ExternalSorting
{
    internal class TestMemory
    {
        public void Func()
        {
            try
            {
                // Пытаемся выделить массив на 200 МБ
                byte[] bigArray = new byte[200 * 1024 * 1024];
                Console.WriteLine("Выделено успешно");
            }
            catch (OutOfMemoryException e)
            {
                Console.WriteLine("Ошибка памяти: " + e.Message);
            }
        }
    }
}
