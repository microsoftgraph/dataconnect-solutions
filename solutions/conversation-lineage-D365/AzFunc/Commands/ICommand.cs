using System.Threading.Tasks;

namespace GDC.Commands
{
    public interface ICommand<T>
    {
        T Execute();
    }

    public interface ICommandAsync<T>
    {
        Task<T> ExecuteAsync();
    }

    public interface ICommandAsync
    {
        Task ExecuteAsync();
    }
}
