using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ExternalSorting
{
    internal class MemoryUsage
    {
        private const long LIMIT = 150L * 1024 * 1024; // 150 МБ в байтах

        public void CheckMemory()
        {
            long used = GC.GetTotalMemory(false);
            Console.WriteLine($"Memory used: {used / (1024 * 1024)} МБ");

            /*if (used > LIMIT)
            {
                throw new OutOfMemoryException($"Превышен лимит 150 МБ: {used / (1024 * 1024)} МБ");
            }*/
        }
    }
}
